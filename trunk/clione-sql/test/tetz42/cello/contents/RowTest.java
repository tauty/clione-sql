package tetz42.cello.contents;


public class RowTest {

}

class Foo{
	int fooInt;
	String fooStr;
	Bar bar;
	CellUnitMap<Bar> bars = CellUnitMap.create(Bar.class);
}

class Bar{
	int barInt;
	String barStr;
	CellUnitMap<Baz> bazzes = CellUnitMap.create(Baz.class);
}

class Baz{
	int bazInt;
	String bazStr;
}
