
package net.sourceforge.pmd.dcd.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sourceforge.pmd.dcd.ClassLoaderUtil;
import net.sourceforge.pmd.util.filter.Filter;


public class UsageGraph implements NodeVisitorAcceptor {

	private final List<ClassNode> classNodes = new ArrayList<ClassNode>();

	protected final Filter<String> classFilter;

	public UsageGraph(Filter<String> classFilter) {
		this.classFilter = classFilter;
	}

	public Object accept(NodeVisitor visitor, Object data) {
		for (ClassNode classNode : classNodes) {
			visitor.visit(classNode, data);
		}
		return data;
	}

	public boolean isClass(String className) {
		checkClassName(className);
		return Collections.binarySearch(classNodes, className, ClassNodeComparator.INSTANCE) >= 0;
	}

	public ClassNode defineClass(String className) {
		checkClassName(className);
		int index = Collections.binarySearch(classNodes, className, ClassNodeComparator.INSTANCE);
		ClassNode classNode;
		if (index >= 0) {
			classNode = classNodes.get(index);
		} else {
			classNode = new ClassNode(className);
			classNodes.add(-(index + 1), classNode);
		}
		return classNode;
	}

	public FieldNode defineField(String className, String name, String desc) {
		ClassNode classNode = defineClass(className);
		return classNode.defineField(name, desc);
	}

	public MemberNode defineConstructor(String className, String name, String desc) {
		ClassNode classNode = defineClass(className);
		return classNode.defineConstructor(name, desc);
	}

	public MemberNode defineMethod(String className, String name, String desc) {
		ClassNode classNode = defineClass(className);
		if (ClassLoaderUtil.CLINIT.equals(name) || ClassLoaderUtil.INIT.equals(name)) {
			return classNode.defineConstructor(name, desc);
		} else {
			return classNode.defineMethod(name, desc);
		}
	}

	public void usageField(String className, String name, String desc, MemberNode usingMemberNode) {
		checkClassName(className);
		if (classFilter.filter(className)) {
			FieldNode fieldNode = defineField(className, name, desc);
			usage(fieldNode, usingMemberNode);
		}
	}

	public void usageMethod(String className, String name, String desc, MemberNode usingMemberNode) {
		checkClassName(className);
		if (classFilter.filter(className)) {
			MemberNode memberNode;
			if (ClassLoaderUtil.CLINIT.equals(name) || ClassLoaderUtil.INIT.equals(name)) {
				memberNode = defineConstructor(className, name, desc);
			} else {
				memberNode = defineMethod(className, name, desc);
			}
			usage(memberNode, usingMemberNode);
		}
	}

	private void usage(MemberNode use, MemberNode user) {
		use.addUser(user);
		user.addUse(use);
	}

	private final void checkClassName(String className) {
		
		if (className.indexOf('/') >= 0 || className.indexOf('\\') >= 0) {
			throw new IllegalArgumentException("Invalid class name: " + className);
		}
	}
}
