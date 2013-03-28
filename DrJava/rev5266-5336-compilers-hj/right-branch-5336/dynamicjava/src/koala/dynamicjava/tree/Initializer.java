

package koala.dynamicjava.tree;



public abstract class Initializer extends Node {
    
    private BlockStatement block;

    
    protected Initializer(BlockStatement block,
			  SourceInfo si) {
	super(si);

	if (block == null) throw new IllegalArgumentException("block == null");

	this.block  = block;
    }
    
    
    public BlockStatement getBlock() {
	return block;
    }

    
    public void setBlock(BlockStatement bs) {
	if (bs == null) throw new IllegalArgumentException("bs == null");
	block = bs;
    }
}
