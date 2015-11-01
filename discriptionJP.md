This document is written in Japanese.<br>
If you can not understand Japanese and want to read this document, please register issues.<br>
I will translate this document in English if the issues are registered.<br>

<h1>Introduction</h1>

clione-sqlの使用方法です。<br>
基本編、応用編に分かれています。<br>
開発を始めるには、まずは基本編に目を通しておけば充分でしょう。<br>
基本編の内容では対応できない問題が発生した場合には、応用編を読んで対応してください。<br>

<h1>基本編</h1>
<h2>1. 「2WaySQL」とは</h2>
まずは簡単なサンプルを使用して、2WaySQLの概念を説明します。<br>
下記のようなsqlファイルを用意します。<br>
<br>
[Sample.sql]<br>
<pre><code>SELECT<br>
    *<br>
FROM<br>
    TABLE1<br>
WHERE<br>
    FIELD1 = /* param1 */100<br>
    AND FIELD2 = /* param2 */'AAA'<br>
</code></pre>

これを適切なパス(後述)に置き、Javaで次のように実装します。<br>
<br>
<pre><code>import static tetz42.clione.SQLManager.*;<br>
           :<br>
    public List&lt;Entity&gt; findTable1ByParam1AndParam2(int param1, String param2) {<br>
        return sqlManager().useFile(getClass(), "Sample.sql")<br>
                .findAll( Entity.class, params("param1", param1).$("param2", param2) );<br>
    }<br>
</code></pre>

すると、上記Sample.sqlは下記のように変換され、適切にparam1, 2の値がbindされて実行されます。<br>
<pre><code>SELECT<br>
    *<br>
FROM<br>
    TABLE1<br>
WHERE<br>
    FIELD1 = ?<br>
    AND FIELD2 = ?<br>
</code></pre>
<br>
Sample.sqlのテンプレート内で使用されている「/<code>*</code> param1 <code>*</code>/」はANSIで定められたSQL文のコメントであるため、Sample1.sqlはそのまま通常のSQLとして実行することができます。このように、テンプレートでありながらも通常のSQL文としても扱える仕組みのことを、<b>2WaySQL</b> と言います。<br>
この仕組みによりclione-sqlでは、テンプレートとなるSQL文を実際に実行したり解析したりしながら開発＆リファクタリングすることが可能となります。<br>
<br>
※上の説明を読んだだけではピンとこないかも知れませんが、実際にこの2WaySQLという仕組みを体験すると、その便利さに驚くと思います。<br>
いつでも実行できるSQLファイルをテンプレートを使うと、リファクタリングもデバッグもとても効率よく行うことができます。<br>
ちなみにこの2WaySQL、発祥はあの有名なSeasar2です。<br>

<h2>2. パラメタの値に応じたSQL文の変化</h2>
clione-sqlでは、パラメタの値に応じて自動的にSQL文を変化させることができます。<br>
例として、下記のようなケースを考えます。<br>
<pre><code>SELECT<br>
    *<br>
FROM<br>
    TABLE1<br>
WHERE<br>
    FIELD1 IN /* param */('aaa', 'bbb', 'ccc')<br>
</code></pre>
paramの値がsizeが5の配列(or List)だった場合、SQL文は下記のように変換されます。<br>
<pre><code>SELECT<br>
    *<br>
FROM<br>
    TABLE1<br>
WHERE<br>
    FIELD1 IN (?, ?, ?, ?, ?)<br>
</code></pre>
このとき、配列(or List)の保持する値は順番どおり正しくbindされます。<br>
また、INで判定したい値に固定のものがあった場合には、下記のように書くことも可能です。<br>
<pre><code>SELECT<br>
    *<br>
FROM<br>
    TABLE1<br>
WHERE<br>
    FIELD1 IN ('aaa', 'bbb', /* param */'ccc')<br>
</code></pre>
この場合も、paramの値がsizeが5の配列(or List)だったら、下記のように変換され、保持する値も正しくbindされます。<br>
<pre><code>SELECT<br>
    *<br>
FROM<br>
    TABLE1<br>
WHERE<br>
    FIELD1 IN ('aaa', 'bbb', ?, ?, ?, ?, ?)<br>
</code></pre>
<br>
更に、SQLファイルで下記のように「=」の前にパラメタを記述すると、パラメタの値に合わせて条件文ごとSQL文を変化させることができます。<br>
<pre><code>SELECT<br>
    *<br>
FROM<br>
    TABLE1<br>
WHERE<br>
    FIELD1 /* param */= 100<br>
</code></pre>

paramがnullのときは、下記のように「IS NULL」に変化します。<br>
<pre><code>SELECT<br>
    *<br>
FROM<br>
    TABLE1<br>
WHERE<br>
    FIELD1 IS NULL<br>
</code></pre>
<br>
paramが配列(or List)でsizeが2以上の場合には、下記のようにIN句に変化します。<br>
<pre><code>SELECT<br>
    *<br>
FROM<br>
    TABLE1<br>
WHERE<br>
    FIELD1 IN (?, ?, ?, ?, ?)<br>
</code></pre>
※配列(or List)の要素数が1のときには「=」に、0の時には「IS NULL」に変化します。<br>
<br>
更にOracleの場合には、IN句に1000を超えるパラメタを指定できないため、1000を超えたら自動的に下記のように1000件ずつに分けて、ORを併用して展開します。<br>
<pre><code>SELECT<br>
    *<br>
FROM<br>
    TABLE1<br>
WHERE<br>
    FIELD1 IN (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?....)<br>
    OR FIELD1 IN (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?....)<br>
    OR FIELD1 IN (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)<br>
</code></pre>

上記のように動作するのは、パラメタを「=, IS, IN, <>, !=, IS NOT, NOT IN」等の比較演算子の前においた場合です。<br>
なお「<>」など否定の比較演算子の前にパラメタを置いた場合には、nullの時には「IS NOT NULL」に、Listか配列の時には「NOT IN」に、それぞれ変化します。<br>
<br>
「LIKE」の前にパラメタを置いた場合もほぼ同じ動作をしますが、パラメタがList or 配列のときの挙動が違います。<br>
デフォルトでは、下記のように「OR」に展開します。<br>
<pre><code>SELECT<br>
    *<br>
FROM<br>
    TABLE1<br>
WHERE<br>
    FIELD1 LIKE ?<br>
    OR FIELD1 LIKE ?<br>
    OR FIELD1 LIKE ?<br>
    OR FIELD1 LIKE ?<br>
    OR FIELD1 LIKE ?<br>
</code></pre>

パラメタを調整することで「AND」に展開させることもできますが、これについては後述します。<br>

<h2>3. SQLファイルの配置</h2>
SQLファイルは基本的にクラスパスの通ったディレクトリ以下のどこかに配置します。<br>
配置した場所のパスを、下記のように指定します。<br>
<pre><code>import static tetz42.clione.SQLManager.*;<br>
           :<br>
        sqlManager().useFile("com/sql/Sample.sql")<br>
                .find ...(省略)<br>
    }<br>
</code></pre>
またSQLファイルをクラスと関連付けて配置したいときのために、SQLManager#useFileメソッドではClassオブジェクトをパラメタに渡せるようになっています。<br>
<pre><code>import static tetz42.clione.SQLManager.*;<br>
           :<br>
        sqlManager().useFile(SampleDao.class, "Sample.sql")<br>
                .find ...(省略)<br>
    }<br>
</code></pre>
上記の例では、例えば<code>SampleDao</code>のパッケージが「com.clione.dao」だったとすると、<br>
<ul><li>com/clione/dao/sql/<code>SampleDao</code>/Sample.sql<br>
に配置されているとみなされます。つまり、<br>
</li><li>[渡されたClassオブジェクトのパッケージ]/sql/[クラス名]/[指定されたファイル名]<br>
というパスだと解釈されますので、そのパスに適切にSQLファイルを置くようにしてください。</li></ul>

<h2>4. ファイルを使用しないSQL</h2>
SQLファイルを作成するほどでもない簡単なSQL文の場合には、下記のように直接実行することもできます。<br>
<pre><code>import static tetz42.clione.SQLManager.*;<br>
           :<br>
    public void delete() {<br>
        return sqlManager().useSQL("DELETE FROM TABLE1").update();<br>
    }<br>
</code></pre>
もちろん、パラメタも使えます。<br>
<pre><code>import static tetz42.clione.SQLManager.*;<br>
           :<br>
    public Entity findById(String id) {<br>
        return sqlManager().useSQL("SELECT * from TABLE1 WHERE ID=/* id */")<br>
                  .find( Entity.class, params("id", id) );<br>
    }<br>
</code></pre>

なおパラメタに渡すSQL文には、ユーザが入力した値やDBから取得した値などの外部の値を使用しないでください。<br>
上記を守らないと、SQLインジェクションという攻撃に対する脆弱性となってしまう可能性があります。<br>

<h2>5. インデントベースの動的SQL</h2>
clione-sqlの名前の由来は、「Clione-sql is Line and Indent Oriented, NEsted-structured, 2WaySQL library.」です。<br>
つまり「clione-sqlは行志向かつインデント志向で、再帰的な構造を持つ2WaySQLなライブラリです。」ってな感じです。<br>
ここではこの名前の由来となった、行志向かつインデント志向な動的SQLについて説明します。<br>
<br>
まずは下記のSQLファイルをご覧ください。<br>
<pre><code>SELECT<br>
    *<br>
FROM<br>
    people<br>
WHERE<br>
    age &gt;= /* $age_from */25<br>
    AND age &lt;= /* $age_to */50<br>
</code></pre>
最初の例と比べると、パラメタに「$」という記号がついていることが分かると思います。<br>
これをclione-sqlでは <b>パラメタ修飾記号</b> と呼びます。<br>
下記のように、Javaソースでパラメタを指定するときには、パラメタ修飾記号は省略できます。<br>
<pre><code>import static tetz42.clione.SQLManager.*;<br>
        :<br>
    public List&lt;Person&gt; findAllByAge(Integer ageFrom, Integer ageTo) throws SQLException {<br>
        return sqlManager().useFile(getClass(), "Select.sql")<br>
                .findAll(Person.class, params("age_from", ageFrom).$("age_to", ageTo));<br>
    }<br>
</code></pre>
なお、パラメタ修飾記号を省略せずに書いても正常に動作しますが、内部的にはパラメタ修飾記号は除去されます。<br>
<br>
パラメタ修飾記号には幾つか種類があり、それぞれで意味が違います。<br>
「$」は「パラメタの値がnullだったら、行ごと削除する」という意味になります。<br>
※ 厳密には少々違いますが、ここでは説明を簡単にするため上記のように書いています。詳細は次の「negativeとpositive」を参照ください。<br>
よって、もしパラメタage_fromがnullだった場合、SQL文は下記のように変換されます。<br>
<pre><code>SELECT<br>
    *<br>
FROM<br>
    people<br>
WHERE<br>
    age &lt;= ?<br>
</code></pre>
区切りの「AND」も除去されて正しいSQL文になっていることに気がついたでしょうか？<br>
これはclione-sqlが自動的に行っています。<br>
<br>
age_from, age_to共にnullだった場合は下記のようにWHERE句ごと除去されます。。<br>
<pre><code>SELECT<br>
    *<br>
FROM<br>
    people<br>
</code></pre>

<h3>5-1. Clione-SQLのルール</h3>
clione-sqlでは以下のルールに従って、これを実現しています。<br>
<ul><li>インデントに従って、下記のように親ノード・子ノードを定義する<br>
<pre><code>親ノード１<br>
    子ノード１−１<br>
    子ノード１−２<br>
        孫ノード１−２−１<br>
        孫ノード１−２−２<br>
    子ノード１−３<br>
親ノード２<br>
    子ノード２−１<br>
    子ノード２−２<br>
</code></pre>
</li><li>子ノードが全て除去された場合、親ノードも除去される。上記例では、孫ノード１−２−１、孫ノード１−２−２が除去された場合には子ノード１−２が除去され、更に子ノード１−１、子ノード１−３も除去された場合には親ノード１が除去される。<br>
</li><li>隣接するノードで同一インデントのものを纏めて、「ブロック」と呼ぶ。ただし親ノード１、２や、子ノード１−２、１−３のように、間にあるのが子ノードのみである場合は、隣接しているものとみなすので同じブロックに属するノードである。<br>
</li><li>最初の状態でブロックの先頭がSQL文の区切り(「AND」, 「OR」,「,」など)でない場合、ノード除去後のブロックの先頭が区切りになっていたら、その区切りを除去する。</li></ul>

もう一度最初の例に戻って説明すると、<br>
<pre><code>SELECT<br>
    *<br>
FROM<br>
    people<br>
WHERE  -- 親ノード１<br>
    age &gt;= /* $age_from */25      -- 子ノード１−１<br>
    AND age &lt;= /* $age_to */50  -- 子ノード１−２<br>
</code></pre>

子ノード１−１のみが除去された場合ブロックの先頭が区切りではないため、先頭になった子ノード１−２の先頭の「AND」が除去されます。<br>
子ノード１−１、１−２がともに除去されたとき、その親ノードである親ノード１が除去されます。<br>
<br>
また、<br>
<ul><li>親ノードが除去された場合、その親ノードに属する子ノード全てが除去される<br>
というルールもあるため、下記のようにブロック単位でSQL文を切り替えるようなことも可能です。<br>
<pre><code>SELECT<br>
    *<br>
FROM<br>
    employee<br>
WHERE<br>
    title = /* $title */'chief'<br>
    -- %IF useDateEmployed<br>
      AND /* $date_from */'19980401' &lt;= date_employed<br>
      AND date_employed  &lt;= /* $date_to */'20050401'<br>
    -- %ELSE<br>
      AND /* $date_from */'20080401' &lt;= date_of_promotion<br>
      AND date_of_promotion  &lt;= /* $date_to */'20120401'<br>
ORDER BY<br>
    employee_id<br>
</code></pre>
</li></ul><blockquote>※ 「%IF」、「%ELSE」については後述。<br>
<br>
その他にもVersion 0.5.0から、ルールが二つ追加になりました。そのうちの一つが、<br>
<ul><li>Clione-SQLが処理を行う前のブロックの最後がSQL文の区切り(「AND」, 「OR」,「,」など)でない場合に、処理後のブロックの最後が区切りになっていたら、その区切りを除去する。<br>
というものです。このルールにより、<br>
<pre><code>SELECT<br>
    *<br>
FROM<br>
    people<br>
WHERE <br>
    age &gt;= /* $age_from */25 AND<br>
    age &lt;= /* $age_to */50<br>
</code></pre>
のように、区切りを行の最後に持ってくるフォーマットも正しく処理できるようになりました。<br>
上記例で「age_to」がnullだった場合は行ごと削除されて「age >= /<code>*</code> $age_from <code>*</code>/25 AND」がブロックの最後の行になりますが、上記ルールにより行末の「AND」が除去されて、<br>
<pre><code>SELECT<br>
    *<br>
FROM<br>
    people<br>
WHERE <br>
    age &gt;= ?<br>
</code></pre>
というSQL文に変換されます。<br>
<br>
もう一つのルールは、<br>
</li><li>SQL文の区切り(「AND」, 「OR」,「,」など)のみの行は、次の行と結合して一つのノードとして扱う<br>
というものです。このルールにより、<br>
<pre><code>SELECT<br>
    *<br>
FROM<br>
    people<br>
WHERE <br>
    age &gt;= /* $age_from */25<br>
    AND<br>
    age &lt;= /* $age_to */50<br>
</code></pre>
というフォーマットのSQL文も正しく処理できます。<br>
上記は「UNION」、「UNION ALL」を意識したルールで、例えば下記のようなSQL文を例として考えます。<br>
<pre><code>SELECT /* &amp;table1 */<br>
        id<br>
        ,name<br>
        ,type<br>
    FROM<br>
        table1<br>
UNION<br>
SELECT /* &amp;table2 */<br>
        id<br>
        ,name<br>
        ,type<br>
    FROM<br>
        table2<br>
UNION ALL<br>
SELECT /* &amp;table3 */<br>
        id<br>
        ,name<br>
        ,type<br>
    FROM<br>
        table3<br>
</code></pre></li></ul></blockquote>

「UNION」、「UNION ALL」はClione-SQLでは「AND」等と同様に区切りとして扱われます。<br>
よって上記例の「UNION」、「UNION ALL」のみの行は、その下のSELECT句と同一ノードとして扱われます。<br>
SELECTの後ろに「&」というパラメタ修飾記号がついたパラメタがそれぞれついています。<br>
「&」は「$」と同様、「パラメタの値がnullだったら、行ごと削除する」という動作は行いますが、「$」とは違ってプレースホルダの「?」の付与とパラメタのバインドを行いません(※「&」の詳細は後述)。<br>
<br>
上記例でtable1がnullの場合、親ノードが除去されればその子ノードは全て除去されるので、table1のSQL文は全て、除去されて、table2, 3のSQL文が残されます。<br>
このときtable2のSQL文の先頭は「UNION」という区切りなので除去されて、結果下記のようになります。<br>
<pre><code>SELECT <br>
        id<br>
        ,name<br>
        ,type<br>
    FROM<br>
        table2<br>
UNION ALL<br>
SELECT <br>
        id<br>
        ,name<br>
        ,type<br>
    FROM<br>
        table3<br>
</code></pre>
<br>
またtable2がnullだった場合には、table2のSQL文がその直前のUNIONごと除去され下記のようになります。<br>
<pre><code>SELECT <br>
        id<br>
        ,name<br>
        ,type<br>
    FROM<br>
        table1<br>
UNION ALL<br>
SELECT <br>
        id<br>
        ,name<br>
        ,type<br>
    FROM<br>
        table3<br>
</code></pre>

<h3>5-2. インデントについて</h3>
Clione-SQLでは、デフォルトでは4タブ設定のエディタの見た目通りにインデントを計算します。<br>
よってインデントに半角空白とタブが混在していても、エディタでの見た目を確認しながら作業をすれば簡単に正しく動作するSQLファイルが作成可能です。<br>
また例えば8タブなど違うタブ数のエディタで作業をしたければ、設定ファイルで任意の値に設定することができます。(※ 後述)<br>
<br>
SQL文を書くときに、適切な単位で改行したり意味のある単位でインデントするのは、開発効率を上げるために多くの方がやっていることだと思います。<br>
clione-sqlではこれに着目して、<br>
<blockquote>「正しく改行・インデントしてあるSQL文であれば、適切なパラメタ修飾子を付けるだけで安全かつ簡単に動的SQLを実現できる」<br>
という世界観を目指しています。<br></blockquote>

<h3>6. SQL文のパースのルール</h3>
Clione-SQLではSQL文の文字列・括弧を認識して、可能な限りユーザの意図に則った処理の実現を目指しています。<br>
ここでは、Clione-SQLがどのようにSQL文を解釈しているのか学びます。<br>
<br>
<h3>6-1. 文字列リテラルのパース</h3>
ANSIによると、SQL文中に登場する文字列リテラルとしては、下記のようなものが認められています。<br>
<br>
<pre><code>-- 通常の文字列リテラル<br>
'a normal string'<br>
<br>
-- 改行コードを含む文字列リテラル<br>
'a string<br>
which<br>
contains<br>
CRLF'<br>
<br>
-- エスケープを含む文字列リテラル<br>
'It''s a string which contains escape sequence'<br>
</code></pre>

Clione-SQLでは、上記を正しく認識して処理を行います。<br>
よって文字列リテラルの途中で改行していても、エスケープが含まれていても問題なくパースされます。<br>
<br>
例として、下記のSQLでパラメータの「ADDRESS」がnullだった場合を考えます。<br>
<br>
<pre><code>INSERT INTO PEOPLE (<br>
        ID<br>
        ,NAME<br>
        ,ADDRESS /* &amp;ADDRESS */<br>
) VALUES (<br>
        /* ID */'0001'<br>
        ,/* NAME */'Yoko'<br>
        ,/* $ADDRESS */'Ocian-Child''s House<br>
123-4<br>
Dokoka-cho<br>
Asoko-ku<br>
Tokyo pref.<br>
Japan'<br>
)<br>
</code></pre>

文字列リテラルは正しく解釈されますので、上記最後の文字列リテラルには改行コードとエスケープが含まれていますが、Clione-SQLでは改行コードなどを無視し正しく一つの文字列リテラルであると解釈するため、「/<code>*</code> $ADDRESS <code>*</code>/」のある行以下6行を一つの行として解釈します。<br>
<br>
よってこれは、<br>
<pre><code>INSERT INTO PEOPLE (<br>
        ID<br>
        ,NAME<br>
) VALUES (<br>
        ?<br>
        ,?<br>
)<br>
</code></pre>
というSQLに変換されます。<br>
<br>
もし下記のようにエスケープし忘れなどで「'」の整合性が取れていない、不正なSQL文を検出した場合には<a href='http://code.google.com/p/clione-sql/source/browse/trunk/clione-sql/src/tetz42/clione/exception/ClioneFormatException.java'>ClioneFormatException</a>をthrowします。<br>
<br>
<pre><code>SELECT<br>
  *<br>
FROM<br>
  BOOKS<br>
WHERE<br>
  TITLE like /* keyword */'%'s%'<br>
</code></pre>

なおMySQLやPostgreSQLでは、下記のような「\」を使ったエスケープがありますが、現時点のClione-SQL(バージョン0.5.1)では無視されます。<br>
よって、下記は文字列の整合性が取れていないと判断され、上記同様<a href='http://code.google.com/p/clione-sql/source/browse/trunk/clione-sql/src/tetz42/clione/exception/ClioneFormatException.java'>ClioneFormatException</a>がthrowされます。<br>
<br>
<pre><code>SELECT<br>
  *<br>
FROM<br>
  BOOKS<br>
WHERE<br>
  TITLE like /* keyword */'%\'s%'<br>
</code></pre>

<h3>6-2. 括弧のパース</h3>
Cione-SQLでは、括弧も意識してパースが行われています。<br>
<br>
<pre><code>SELECT<br>
	*<br>
FROM<br>
	EMPLOYEE<br>
WHERE<br>
	TITLE = /* $title */'Chief'<br>
	AND (<br>
		ENTERING_DATE &lt;= /* $today */'2012-01-01'<br>
		OR RETIRE_DATE  &gt;= /* $today */'2012-01-01'<br>
	)<br>
</code></pre>

<h3>5-5. パラメタ修飾記号の否定</h3>
パラメタ修飾記号は、「!」を付与することで挙動を逆にすることもできます。<br>
<pre><code>SELECT<br>
    *<br>
FROM<br>
    people<br>
WHERE<br>
    hair_color = 'black'<br>
    AND age /* $!age */= 25<br>
</code></pre>
上記のように書いた場合、ageがnullの時には「age IS NULL」に変換され、ageに値が入っているときには行削除が行われます。<br>

<h2>6. negativeとpositive</h2>
実は上の「インデントベースの動的SQL」の中で書いた、「nullのときには･･･」という表現、厳密には間違っています。<br>
clione-sqlではパラメタの値に関してnegativeかpositiveかを判定します。このnegativeと判定される値の一つが「null」です。<br>
clione-sqlにてnegativeと判定される値は下記の通りです。<br>
<ul><li>null<br>
</li><li>false(より正確には、Boolean.FALSEとequals判定で一致するもの)<br>
</li><li>空の配列 or List<br>
</li><li>要素が全てがnegativeの配列 or List<br>
</li><li>その他、ユーザーがnegativeとして設定した値(※応用編にて後述)<br>
上記以外の値は、全てpositiveとして判定されます。</li></ul>

※以降の説明では、この「negative」「positive」という用語を使います。<br>
<br>
<h2>7. パラメタ修飾記号</h2>
ここで、パラメタ修飾記号について詳しく説明します。<br>
パラメタ修飾記号はパラメタの前につけることで特別な意味を持たせることができる記号のことです。<br>
上でも触れていますが、パラメタとの間に「!」をつけることで意味を逆転させることができます。<br>
<br>
<ul><li>$ ･･･ パラメタがnegativeだったときに行ごと削除します。(前述)<br>
<blockquote>由来：正規表現では行末を表す記号なので、行の制御をつかどらせることにしました。<br>
</blockquote></li><li>& ･･･ パラメタがnegativeだったときに行ごと削除するのは「$」と同じです。違いはパラメタがpositiveのときに<br>
<ol><li>「?」のSQL文への付与<br>
</li><li>値のbind<br>
</li></ol><blockquote>などの処理を行わないことです。下記みたいなケースで使います。<br>
<pre><code>SELECT<br>
    *<br>
FROM<br>
    people<br>
WHERE<br>
    SEX = 'female' /* &amp;!is_gender_free */<br>
    AND age /* $!age */= 25<br>
</code></pre>
<blockquote>由来：上のように、条件分っぽい使い方になると思ったので、「and」を意味するこれを選びました。<br>
</blockquote></blockquote></li><li>@ ･･･ パラメタがnegativeのときに、ParameterNotFoundExceptionをthrowします。必須のパラメタに付与します。<br>
<blockquote>由来：特にないです。なんとなく必須っぽいイメージがわきました。<br>
</blockquote></li><li>? ･･･ パラメタがnegativeのときに、右隣のパラメタを有効とします。右隣のパラメタもnegativeで、更にその右隣のパラメタがなかったら、パラメタの後ろの値を有効とします。つまり、<br>
<pre><code>UPDATE people<br>
SET<br>
    hometown = /* ?prefecture ?country */'unknown'<br>
WHERE<br>
    ID = /* @id */'11'<br>
</code></pre>
<blockquote>となっていた場合、prefecture の値がpositiveならその値が、negativeならcountry が、countryもnegativeなら後ろの値の'unknown'が有効となります。<br>
もし、<br>
<blockquote>hometown = /<code>*</code> ?prefecture ?country <code>*</code>/<br>
</blockquote>と、後ろの値がないときに両方ともnegativeなら、何も起こりません。</blockquote></li></ul>

<blockquote>由来：これも特にないです。イメージです。</blockquote>

<h2>6. SQLコメントの扱い</h2>

<h3>6-1. SQLコメント内の改行</h3>
SQLコメント内部では、自由に改行することができます。<br>
<br>
{{{}}}<br>
<br>
<h3>5-4. 行コメントの扱い</h3>

<h3>5-4. 改行のエスケープ</h3>



<h2>様々なfindメソッド</h2>
// TODO 説明追記<br>
<br>
<h2>INSERT, UPDATE, DELETEを実行するupdateメソッド</h2>
// TODO 説明追記<br>
<br>
<h2>パラメタについて</h2>
<ul><li>paramsメソッド<br>
</li><li>条件bean使用<br>
</li><li>$, $e, $on</li></ul>

<h2>コネクション</h2>
// TODO 説明追記<br>
<br>
<h2>補助関数(%concat, %C, %esc_like, %L)</h2>
clione-sql0.4.0より、補助関数という概念が導入されました。<br>
パラメタ修飾記号だけでは対処が難しいことを実現します。<br>
補助関数には多数種類がありますが、基礎編では4つだけ紹介します。<br>
<br>
<h3>文字列連結補助関数(%concat, %C)</h3>
「%concat」は文字列連結用の補助関数です。与えられた文字列とパラメタを連結して、一つのパラメタとしてまとめる働きがあります。<br>
サンプルとして、下記のSQLファイルをご覧ください。<br>
<pre><code>SELECT<br>
    COUNT(*)<br>
FROM<br>
    people<br>
WHERE<br>
    name like /* %concat('%', part_of_name, '%') */'%愛%'<br>
</code></pre>
これは下記のように変換されます。<br>
<pre><code>SELECT<br>
    COUNT(*)<br>
FROM<br>
    people<br>
WHERE<br>
    name like ?<br>
</code></pre>
part_of_nameの値が「希」だったとすると、bindされるパラメタの値は「%希%」になります。<br>
なお、括弧と括弧内の区切り文字「,」は省略することもできます。またより短くしたい場合には、「%C」が「%concat」と同じ働きをする補助関数なので、こちらを使用してください。下記は上と全く同じ意味のSQLファイルです。<br>
<pre><code>SELECT<br>
    COUNT(*)<br>
FROM<br>
    people<br>
WHERE<br>
    name like /*%C '%' part_of_name '%' */'%愛%'<br>
</code></pre>

%concat, %Cのその他の使い方として、各種DBプロダクトに依存しないで文字列連結を行う、というのがあります。<br>
文字列連結はANSI標準では「string1 || string2」なのですが、MySQLはconcat関数にしか対応していなかったり、SQL Serverは「string1 + string2」だったり、実情は全く統一されていません。<br>
%concat, %Cを使えば下記のように、DBプロダクトに依存しない形で文字列連結を実現することができます。<br>
<pre><code>INSERT INTO people (<br>
    id<br>
    ,name<br>
    ,last_name<br>
    ,full_name<br>
    ,full_name_jp<br>
)<br>
values(<br>
    11<br>
    ,/* name */<br>
    ,/* last_name */<br>
    ,/*%C name ' ' last_name */<br>
    ,/*%C last_name '　' name */<br>
)<br>
</code></pre>

<h3>LIKE句用補助関数(%esc_like, %L)</h3>
LIKE句に値をbindするとき、値の中に'%'や'<code>_</code>'が含まれていると、LIKE句の記号として解釈されてしまうことが知られています。<br>
※ 念のため：'<code>_</code>'はLIKE句では任意の一文字にマッチします。<br>
<br>
よって例えば「そうさ100%病気」をLIKE句で検索した場合、<br>
<ul><li>そうさ100年間病気<br>
</li><li>そうさ100人全部病気<br>
など、想定とは違う検索結果も一緒に返ってきてしまいます。<br>
これは場合によっては、全く関係ない人に重要情報を見せてしまうようなセキュリティバグにつながりかねません。<br>
これを避けるため、ANSIでLIKE句のエスケープを下記のように定めています。<br>
下記は「そうさ100%病気」という文字列が含まれるレコードの件数を数える例です。<br>
LIKE句で意味を持つ'%'の前に'#'を置いてエスケープしています。<br>
<pre><code>SELECT<br>
    COUNT(*)<br>
FROM<br>
    songs<br>
WHERE<br>
    lyrics like '%そうさ100#%病気%' escape '#'<br>
</code></pre>
※ 本来escapeに使う値はユーザが自由に決めて良いのですが、clione-sqlでは「#」に統一しています。<br>
<br>
このエスケープを行う補助関数が%esc_likeです。<br>
下記のようにして使用します。<br>
<pre><code>SELECT<br>
    COUNT(*)<br>
FROM<br>
    songs<br>
WHERE<br>
    lyrics like /*%C '%' %esc_like(part_of_lyrics) '%' */'%そうさ100#%病気%' escape '#'<br>
</code></pre>
part_of_lyricsに渡した値が、自動的に'#'でエスケープされるようになります。<br>
<br>
補助関数「%L」を使うと上記は下記のように、より簡単に書くことができます。<br>
<pre><code>SELECT<br>
    COUNT(*)<br>
FROM<br>
    songs<br>
WHERE<br>
    lyrics like /*%L '%' part_of_lyrics '%' */'%そうさ100#%病気%'<br>
</code></pre>
「%L」は、与えられたパラメタをエスケープし、%concatで連結した上で、SQL文に自動で「escape '#'」を付与します。<br>
また、パラメタの数が下記のように複数になっても、それぞれのパラメタに%esc_likeを適用するので大丈夫です。<br>
<pre><code>SELECT<br>
    COUNT(*)<br>
FROM<br>
    songs<br>
WHERE<br>
    lyrics like /*%L '%' pol1 '_' pol2 '%' */'%そうさ100#%病気%'<br>
</code></pre>
pol1が「君」、pol2が「1000%」だった場合、上記のパラメタは連結されて「%君<code>_</code>1000#%%」に、SQL文は下記のように変換されます。<br>
<pre><code>SELECT<br>
    COUNT(*)<br>
FROM<br>
    songs<br>
WHERE<br>
    lyrics like ? escape '#'<br>
</code></pre>
検索結果として、<br>
</li></ul><ul><li>君が1000%<br>
</li><li>君も1000%<br>
</li><li>君と1000%<br>
などが含まれたレコードの件数が取得できるはずです。<br>
<br>
LIKE句に値をbindするときには、エスケープの必要がなければ%Cを、あれば%Lを使ってください。<br>
いちいち判断するのが面倒なら、「LIKE句だったら必ず『%L』」としても良いでしょう。</li></ul>

<h1>応用編</h1>

<h2>文字列リテラル</h2>
Coming soon!<br>
<pre><code>  'string'<br>
  "field1 = /+ field1 +/"<br>
  :field1 = /+ field1 +/<br>
  |field1 = /* field1 */<br>
</code></pre>

<h2>パラメタの合成</h2>
// TODO 説明の追記<br>
<br>
<h2>コメント</h2>
Coming soon!<br>
<pre><code>-- コメント<br>
  /** 通常のコメントと解釈 */<br>
  -- こちらも通常のコメント<br>
  /*+ Oracleのヒント句 */<br>
  /*! MySQLのヒント句 */<br>
-- パラメタ<br>
  /* param */<br>
  -- &amp;param<br>
</code></pre>

<h2>行の連結</h2>
Coming soon!<br>
<pre><code>  FIELD IN /* values */('aaa', 'bbb', 'ccc' --<br>
      'ddd', 'eee')<br>
  aaa in /**<br>
    この書き方のコメントは、<br>
    複数行に分けて書いても、1行だと判断されます。<br>
    改行文字もコメントアウトされる、と考えると分かり易いと思います。<br>
    */ ('111', '222', '333')<br>
</code></pre>

<h2>SQLExecutor</h2>
<h2>negativeと判定する値の追加</h2>
<h2>propertiesファイル</h2>
<h2>%if-%elseif-%else</h2>
<pre><code>   /*%if cond1 'AAA' %elseif cond2 'bbb' else 'ccc' */'ddd'<br>
</code></pre>
<h2>ブロック切替え</h2>
<pre><code>WHERE<br>
  -- %if cond<br>
    fieldA1 = /* valueA1 */<br>
    AND fieldA2 = /* valueA2 */<br>
  -- %if !cond<br>
    --: fieldB1 = /+ valueB1 +/<br>
    --: AND fieldB2 = /+ valueB2 +/<br>
  /* %if condC<br>
    fieldC1 = /+ valueC1 +/<br>
    AND fieldC2 = /+ valueC2 +/<br>
  */<br>
</code></pre>
<h2>%include</h2>
<pre><code>    /* %include('./Sub_Query') '<br>
        UNION<br>
'      %include('./Sub_Query' %on('option1')) */<br>
</code></pre>
<h2>%STR, %SQL</h2>
// TODO より詳細な説明<br>
JavaからSQLに直接文字列 or clione-sqlにより解釈されたSQL文を書き込むことができる補助関数。<br>
使い方を間違えるとSQLインジェクションをくらう危険性があるので、要注意！<br>
別の手段がどうしてもないときのみ使用し、使用する場合もなるべくJavaの固定値(static finalな値とかenumとか)を渡すようにしてください。<br>
間違っても、ユーザの入力した値をノーチェックで渡さないこと！<br>
※警戒の意味を込めて、この二つの補助関数だけは1文字でもないのに大文字で表記しています。<br>
<pre><code>    /* %STR(param1) */<br>
    /* %SQL(param2) */<br>
</code></pre>