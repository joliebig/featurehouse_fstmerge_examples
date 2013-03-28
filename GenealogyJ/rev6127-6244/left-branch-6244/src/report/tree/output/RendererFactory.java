

package tree.output;

import tree.IndiBox;
import tree.Translator;
import tree.graphics.FooterRenderer;
import tree.graphics.GraphicsRenderer;
import tree.graphics.TitleRenderer;


public class RendererFactory {
    public TreeRendererBase renderer;
    public GraphicsRenderer rotateRenderer;
    public GraphicsRenderer titleRenderer;
    public FooterRenderer footerRenderer;

    public RendererFactory(Translator translator)
    {
        renderer = new VerticalTreeRenderer();
        rotateRenderer = new RotateRenderer(renderer);
        titleRenderer = new TitleRenderer(rotateRenderer);
        footerRenderer = new FooterRenderer(titleRenderer, translator);
    }

    public GraphicsRenderer createRenderer(IndiBox firstIndi, TreeElements elements)
    {
        renderer.setElements(elements);
        renderer.setFirstIndi(firstIndi);
        footerRenderer.setFirstIndi(firstIndi);

        return footerRenderer;
    }

}
