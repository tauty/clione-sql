package tetz42.clione.lang;



public class NumInstruction extends Instruction {

	@Override
	public Instruction toInstruction() {
		Instruction inst = new Instruction();
		inst.map = this.map;
		inst.next = this.next;
		inst.params = this.params;
		inst.status = this.status;
		inst.doNothing = this.doNothing;
		inst.replacement = this.replacement;
		inst.isNodeDisposed = this.isNodeDisposed;
		inst.useValueInBack = this.useValueInBack;
		return inst;
	}
}
