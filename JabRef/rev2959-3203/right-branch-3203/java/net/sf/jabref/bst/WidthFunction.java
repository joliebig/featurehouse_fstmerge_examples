package net.sf.jabref.bst;

import java.util.Stack;

import net.sf.jabref.bst.VM.BstEntry;
import net.sf.jabref.bst.VM.BstFunction;




public class WidthFunction implements BstFunction {

	VM vm;

	public WidthFunction(VM vm) {
		this.vm = vm;
	}

	public void execute(BstEntry context) {
		Stack<Object> stack = vm.getStack();

		if (stack.size() < 1) {
			throw new VMException("Not enough operands on stack for operation width$");
		}
		Object o1 = stack.pop();

		if (!(o1 instanceof String)) {
			vm.warn("A string is needed for change.case$");
			stack.push(new Integer(0));
			return;
		}
		
		stack.push(new Integer(BibtexWidth.width((String) o1, vm)));
	}
}
