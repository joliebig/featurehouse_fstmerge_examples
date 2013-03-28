package net.sourceforge.pmd.ast;




public interface AccessNodeInterface {

	int PUBLIC = 0x0001;
	int PROTECTED = 0x0002;
	int PRIVATE = 0x0004;
	int ABSTRACT = 0x0008;
	int STATIC = 0x0010;
	int FINAL = 0x0020;
	int SYNCHRONIZED = 0x0040;
	int NATIVE = 0x0080;
	int TRANSIENT = 0x0100;
	int VOLATILE = 0x0200;
	int STRICTFP = 0x1000;

	int getModifiers();

	void setModifiers(int modifiers);

	boolean isPublic();

	
	void setPublic();

	void setPublic(boolean isPublic);

	boolean isProtected();

	
	void setProtected();

	void setProtected(boolean isProtected);

	boolean isPrivate();

	
	void setPrivate();

	void setPrivate(boolean isPrivate);

	boolean isAbstract();

	
	void setAbstract();

	void setAbstract(boolean isAbstract);

	boolean isStatic();

	
	void setStatic();

	void setStatic(boolean isStatic);

	boolean isFinal();

	
	void setFinal();

	void setFinal(boolean isFinal);

	boolean isSynchronized();

	
	void setSynchronized();

	void setSynchronized(boolean isSynchronized);

	boolean isNative();

	
	void setNative();

	void setNative(boolean isNative);

	boolean isTransient();

	
	void setTransient();

	void setTransient(boolean isTransient);

	boolean isVolatile();

	
	void setVolatile();

	void setVolatile(boolean isVolatile);

	boolean isStrictfp();

	
	void setStrictfp();

	void setStrictfp(boolean isStrictfp);

	boolean isPackagePrivate();
}
