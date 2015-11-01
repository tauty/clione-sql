Clione-SQL stands for Clione-SQL is Line and Indent Oriented, NEsted-structured, 2WaySQL library.

## How it works? ##

ClioneSQL works on 4 simple rules.

Rule 1. ClioneSQLは1行単位で処理をする。これを以下ではLineUnitと呼ぶ。<br>
Rule 2. LineUnitはインデントにより下記のように入れ子構造の親子関係が定義される。<br>
<pre><code>  WHERE -- parent<br>
    ROW1 = /* $key1 */'value1'     -- child1<br>
    AND ROW2 = /* $key2 */'value2' -- child2<br>
    AND (                          -- child3<br>
      ROW3_1 = /* $key3_1 */'value3_1'         -- child of child3<br>
      OR ROW3_2 =/* $key3_2 */'value3_2'       -- child of child3<br>
    )<br>
<br>
</code></pre>
Rule 3. LineUnitが持つ子が全て除去されたら、そのLineUnitも除去される。上の例では、key3_1, key3_2の両方がnullなら、child3も除去される。key1, key2もnullなら、parentごと除去される。<br>
Rule 4. 親LineUnitの後に来る最初の子LineUnitの先頭にセパレータ(AND/OR/,)があるとき、それは除去される。上の例では、key3_1がnullで除去された場合には下のkey3_2の先頭の「,」が除去される。key1がnullで除去された場合には、その下のkey2の先頭の「AND」が除去される。<br>
<br>
このように、SQLファイルのインデントを調整すれば、サブクエリーなどを持つ複雑なSQL文であっても、動的にな操作が簡単かつ安全に実現できる。<br>
<br>

<h2>その他</h2>

<h3>SQLパラメータ</h3>
SQLパラメータとして使用するSQLコメントは、コメント先頭の記号に応じて挙動が変わる。<br>
<ol><li>記号なし /<code>*</code> KEY1 <code>*</code>/'VALUE1'<br>
<blockquote>KEY1がnullでもそのままバインドされる。通常のO/Rマッパーと同じ挙動。<br>
</blockquote></li></ol><blockquote>2. 「$」 /<code>*</code> $KEY2 <code>*</code>/'VALUE2'<br>
<blockquote>「$」がついている場合には、KEY2がnullでなければプレースホルダーに置換され、nullなら行ごと除去される。<br>
</blockquote>3. 「&」 /<code>*</code> &KEY3 <code>*</code>/<br>
<blockquote>「&」がついている場合には、KEY2がnullでなければ何も起こらず、nullなら行ごと除去される。<br>
</blockquote>4. 必須記号「@」 /<code>*</code> @KEY4 <code>*</code>/'VALUE4'<br>
<blockquote>「@」がついている場合には、KEY4がnullでなければプレースホルダーに置換され、nullなら例外(ParameterNotFoundException)が発生する。<br>
</blockquote>5. デフォルトあり記号「?」 /<code>*</code> ?KEY1 <code>*</code>/'VALUE1'<br>
<blockquote>記号がついていない場合には、KEY1がnullでなければプレースホルダーに置換され、nullなら置換が実施されない。(上記'VALUE1'がデフォルト値になる)</blockquote></blockquote>

なお、SQLパラメータと後続の値との間は空けてはならない。もし空けると、例外(SQLFormatException)が発生する(「&」の場合は後続の値が必要ないので、除く)。<br>
<br>
また、SQLパラメータのKEY名の先頭の記号を除いた部分は/[A-Za-z0-9-<i>.]+/で構成する。<br>
※現状はそれ以外の文字(例えば空白)が入っていても動作しますが、将来非対応になる予定です。</i>

<h3>find, findAllメソッド</h3>
findAllメソッドはSELECT文を発行して取得した値全てをListで返します。<br>
<pre><code>import static tetz42.clione.SQLManager.*;<br>
              :<br>
	public List&lt;Entity&gt; findAllByAgeAndPref(Integer age, String pref) throws SQLException {<br>
		return sqlManager().useFile(getClass(), "Select.sql")<br>
			.findAll(Entity.class, params("age", age).$("pref", pref));<br>
	}<br>
</code></pre>
結果が取得できなかった場合には空のListを返却します。<br>
<br>
findメソッドは一件のみ返します。もしSELECT文で複数件取得された場合も最初に取得された一件のみを返します。<br>
<pre><code>import static tetz42.clione.SQLManager.*;<br>
              :<br>
	public Entity findByAgeAndPref(Integer age, String pref) throws SQLException {<br>
		return sqlManager().useFile(getClass(), "Select.sql")<br>
			.find(Entity.class, params("age", age).$("pref", pref));<br>
	}<br>
</code></pre>
結果が取得できなかった場合にはnullを返却します。<br>
<br>
パラメタの指定方法は二通りあります。<br>
<br>
<BR><br>
<br>
<br>
#JavaBeanを渡す方法<br>
<pre><code>import static tetz42.clione.SQLManager.*;<br>
              :<br>
	public Entity findByAgeAndPref(Integer age, String pref) throws SQLException {<br>
		ParamBean bean = new ParamBean();<br>
		bean.setAge(age);<br>
		bean.setPref(pref);<br>
		return sqlManager().useFile(getClass(), "Select.sql")<br>
			.find(Entity.class, bean);<br>
	}<br>
</code></pre>
この場合、JavaBeanのフィールド名でSQLファイルとマッピングして、パラメタがバインドされます。<br>
<br>
#Map<String, Object>を渡す方法<br>
<pre><code>import static tetz42.clione.SQLManager.*;<br>
              :<br>
	public Entity findByAgeAndPref(Integer age, String pref) throws SQLException {<br>
		Map&lt;String, Object&gt; params = new HashMap&lt;String, Object&gt;();<br>
		params.put("age", age);<br>
		params.put("pref", pref);<br>
		return sqlManager().useFile(getClass(), "Select.sql")<br>
			.find(Entity.class, params);<br>
	}<br>
</code></pre>
この場合はキー名でSQLファイルとマッピングして、パラメタがバインドされます。<br>
SQLManager.paramsメソッドをヘルパーとして使用すれば、下記のようにパラメタが間単に構築できます。<br>
<pre><code>import static tetz42.clione.SQLManager.*;<br>
              :<br>
	public Entity findByAgeAndPref(Integer age, String pref) throws SQLException {<br>
		return sqlManager().useFile(getClass(), "Select.sql")<br>
			.find(Entity.class, params("age", age).$("pref", pref));<br>
	}<br>
</code></pre>
<br>
どのケースでも、パラメタの先頭の記号は無視してマッピングされます。つまり、params("$key", "value")とparams("key", "value")とparams("@key", "value")は全て同じ意味になります。<br>
<br>
また、パラメタが必要ない場合には、省略できます。<br>
<pre><code>import static tetz42.clione.SQLManager.*;<br>
              :<br>
	public Entity find() throws SQLException {<br>
		return sqlManager().useFile(getClass(), "Select.sql")<br>
			.find(Entity.class);<br>
	}<br>
</code></pre>


<h3>updateメソッド</h3>


<h3>eachメソッド</h3>
何万件、何十万件といった膨大なレコードを扱う場合、findAllメソッドを使用するとメインメモリに大量のデータを保持することになってしまいます。<br>
こういった場合には、eachメソッドを使用します。<br>
<pre><code>import static tetz42.clione.SQLManager.*;<br>
              :<br>
	public Entity find() throws SQLException {<br>
<br>
		SQLManager sqlManager = sqlManager();<br>
		try {<br>
			for (Entity entity : sqlManager.useFile(getClass(), "Select.sql").each(Entity.class, params("pref", "Newyork"))) {<br>
				sqlManager.useFile(getClass(), "Update.sql").update(entity);<br>
			}<br>
		} finally {<br>
			sqlManager.closeStatement();<br>
		}<br>
	}<br>
</code></pre>


<h3>emptyAsNegativeメソッド</h3>
例えばWebページで検索条件をテキストフィールドで入力させる場合など、ユーザが未入力の場合に空文字が取得されるケースもあります。<br>
こういった場合にnullだけではなく空文字でもwhere句から外させるためには、emptyAsNegativeメソッドが使えます。<br>
<pre><code>import static tetz42.clione.SQLManager.*;<br>
              :<br>
	public List&lt;Entity&gt; findAllByAgeAndPref(Integer age, String pref) throws SQLException {<br>
		return sqlManager().emptyAsNegative().useFile(getClass(), "Select.sql")<br>
			.findAll(Entity.class, params("age", age).$("pref", pref));<br>
	}<br>
</code></pre>
emptyAsNegativeとは別に、asNegativeメソッドといものもあり、こちらは複数のObject型の値を渡せるので、例えば「asNegative("", "<code>*</code>", 0)」と呼び出して「空文字と'<code>*</code>'と0をnullとみなす」といった処理をさせることも可能です。<br>
<br>
<h3>Collection、配列</h3>
パラメータの値がCollection(List、Set等のこと)か配列の場合には、格納された要素分のプレースホルダーに置換されて、正しくバインドされて実行される。<br>
例：<br>
<pre><code>SELECT<br>
  *<br>
FROM<br>
  TABLE1<br>
WHERE<br>
  ROW1 IN /* KEY */('val1', 'val2', 'val3')<br>
</code></pre>

KEYが要素数5の配列の場合、下記のように変換される。<br>
<pre><code>SELECT<br>
  *<br>
FROM<br>
  TABLE1<br>
WHERE<br>
  ROW1 IN (?, ?, ?, ?, ?)<br>
</code></pre>

<h3>LineUnitの結合</h3>
LineUnitとして処理させたい纏まりが長すぎる場合には、下記のように行末に行コメントを書けば、次の行も合わせてLineUnitとして扱う。<br>
<pre><code>    ROW1 IN (/* @key1 */'value1', /* @key2 */'value2', /* @key3 */'value3' --<br>
             ,　/* @key4 */'value4', /* @key5 */'value5')<br>
<br>
</code></pre>

<h3>コメントを書きたいとき</h3>
ClioneSQLでは、下記のように書けばSQLパラメータではなく、通常のコメントだと解釈する。<br>
<pre><code>/** 通常はこれを使う */<br>
/*+ Oracleのヒント句 */<br>
/*! MySQLのヒント句 */<br>
-- 行コメントは普通に書けますが、将来拡張する予定なので「--」の後に空白を入れるようにしておいてください。<br>
</code></pre>