package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Tree.Node;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.Layout;
import com.badlogic.gdx.scenes.scene2d.utils.Selection;
import com.badlogic.gdx.scenes.scene2d.utils.UIUtils;
import com.badlogic.gdx.utils.Array;

public class Tree<N extends Node, V> extends WidgetGroup {
    private static final Vector2 tmp = new Vector2();
    private ClickListener clickListener;
    private N foundNode;
    float iconSpacingLeft;
    float iconSpacingRight;
    float indentSpacing;
    private N overNode;
    float paddingLeft;
    float paddingRight;
    private float prefHeight;
    private float prefWidth;
    N rangeStart;
    final Array<N> rootNodes;
    final Selection<N> selection;
    private boolean sizeInvalid;
    TreeStyle style;
    float ySpacing;

    public Tree(Skin skin) {
        this((TreeStyle) skin.get(TreeStyle.class));
    }

    public Tree(Skin skin, String styleName) {
        this((TreeStyle) skin.get(styleName, TreeStyle.class));
    }

    public Tree(TreeStyle style2) {
        this.rootNodes = new Array<>();
        this.ySpacing = 4.0f;
        this.iconSpacingLeft = 2.0f;
        this.iconSpacingRight = 2.0f;
        this.sizeInvalid = true;
        this.selection = new Selection<N>() {
            /* access modifiers changed from: protected */
            public void changed() {
                int size = size();
                if (size == 0) {
                    Tree.this.rangeStart = null;
                } else if (size == 1) {
                    Tree.this.rangeStart = (Node) first();
                }
            }
        };
        this.selection.setActor(this);
        this.selection.setMultiple(true);
        setStyle(style2);
        initialize();
    }

    private void initialize() {
        AnonymousClass2 r0 = new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                N node = Tree.this.getNodeAt(y);
                if (node == null || node != Tree.this.getNodeAt(getTouchDownY())) {
                    return;
                }
                if (!Tree.this.selection.getMultiple() || !Tree.this.selection.notEmpty() || !UIUtils.shift()) {
                    if (node.children.size > 0 && (!Tree.this.selection.getMultiple() || !UIUtils.ctrl())) {
                        float rowX = node.actor.getX();
                        if (node.icon != null) {
                            rowX -= Tree.this.iconSpacingRight + node.icon.getMinWidth();
                        }
                        if (x < rowX) {
                            node.setExpanded(!node.expanded);
                            return;
                        }
                    }
                    if (node.isSelectable()) {
                        Tree.this.selection.choose(node);
                        if (!Tree.this.selection.isEmpty()) {
                            Tree.this.rangeStart = node;
                            return;
                        }
                        return;
                    }
                    return;
                }
                if (Tree.this.rangeStart == null) {
                    Tree.this.rangeStart = node;
                }
                N rangeStart = Tree.this.rangeStart;
                if (!UIUtils.ctrl()) {
                    Tree.this.selection.clear();
                }
                float start = rangeStart.actor.getY();
                float end = node.actor.getY();
                if (start > end) {
                    Tree tree = Tree.this;
                    tree.selectNodes(tree.rootNodes, end, start);
                } else {
                    Tree tree2 = Tree.this;
                    tree2.selectNodes(tree2.rootNodes, start, end);
                    Tree.this.selection.items().orderedItems().reverse();
                }
                Tree.this.selection.fireChangeEvent();
                Tree.this.rangeStart = rangeStart;
            }

            public boolean mouseMoved(InputEvent event, float x, float y) {
                Tree tree = Tree.this;
                tree.setOverNode(tree.getNodeAt(y));
                return false;
            }

            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                super.enter(event, x, y, pointer, fromActor);
                Tree tree = Tree.this;
                tree.setOverNode(tree.getNodeAt(y));
            }

            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                super.exit(event, x, y, pointer, toActor);
                if (toActor == null || !toActor.isDescendantOf(Tree.this)) {
                    Tree.this.setOverNode(null);
                }
            }
        };
        this.clickListener = r0;
        addListener(r0);
    }

    public void setStyle(TreeStyle style2) {
        this.style = style2;
        if (this.indentSpacing == 0.0f) {
            this.indentSpacing = plusMinusWidth();
        }
    }

    public void add(N node) {
        insert(this.rootNodes.size, node);
    }

    public void insert(int index, N node) {
        int existingIndex = this.rootNodes.indexOf(node, true);
        if (existingIndex != -1 && existingIndex < index) {
            index--;
        }
        remove(node);
        node.parent = null;
        this.rootNodes.insert(index, node);
        node.addToTree(this);
        invalidateHierarchy();
    }

    public void remove(N node) {
        if (node.parent != null) {
            node.parent.remove(node);
            return;
        }
        this.rootNodes.removeValue(node, true);
        node.removeFromTree(this);
        invalidateHierarchy();
    }

    public void clearChildren() {
        super.clearChildren();
        setOverNode((Node) null);
        this.rootNodes.clear();
        this.selection.clear();
    }

    public Array<N> getNodes() {
        return this.rootNodes;
    }

    public void invalidate() {
        super.invalidate();
        this.sizeInvalid = true;
    }

    private float plusMinusWidth() {
        float width = Math.max(this.style.plus.getMinWidth(), this.style.minus.getMinWidth());
        if (this.style.plusOver != null) {
            width = Math.max(width, this.style.plusOver.getMinWidth());
        }
        if (this.style.minusOver != null) {
            return Math.max(width, this.style.minusOver.getMinWidth());
        }
        return width;
    }

    private void computeSize() {
        this.sizeInvalid = false;
        this.prefWidth = plusMinusWidth();
        this.prefHeight = 0.0f;
        computeSize(this.rootNodes, 0.0f, this.prefWidth);
        this.prefWidth += this.paddingLeft + this.paddingRight;
    }

    private void computeSize(Array<N> nodes, float indent, float plusMinusWidth) {
        float rowWidth;
        float ySpacing2 = this.ySpacing;
        float spacing = this.iconSpacingLeft + this.iconSpacingRight;
        int n = nodes.size;
        for (int i = 0; i < n; i++) {
            N node = (Node) nodes.get(i);
            float rowWidth2 = indent + plusMinusWidth;
            A a = node.actor;
            if (a instanceof Layout) {
                Layout layout = (Layout) a;
                rowWidth = rowWidth2 + layout.getPrefWidth();
                node.height = layout.getPrefHeight();
            } else {
                rowWidth = rowWidth2 + a.getWidth();
                node.height = a.getHeight();
            }
            if (node.icon != null) {
                rowWidth += node.icon.getMinWidth() + spacing;
                node.height = Math.max(node.height, node.icon.getMinHeight());
            }
            this.prefWidth = Math.max(this.prefWidth, rowWidth);
            this.prefHeight += node.height + ySpacing2;
            if (node.expanded) {
                computeSize(node.children, this.indentSpacing + indent, plusMinusWidth);
            }
        }
    }

    public void layout() {
        if (this.sizeInvalid) {
            computeSize();
        }
        layout(this.rootNodes, this.paddingLeft, getHeight() - (this.ySpacing / 2.0f), plusMinusWidth());
    }

    private float layout(Array<N> nodes, float indent, float y, float plusMinusWidth) {
        float ySpacing2 = this.ySpacing;
        float spacing = this.iconSpacingLeft + this.iconSpacingRight;
        int n = nodes.size;
        for (int i = 0; i < n; i++) {
            N node = (Node) nodes.get(i);
            float x = indent + plusMinusWidth;
            if (node.icon != null) {
                x += node.icon.getMinWidth() + spacing;
            }
            if (node.actor instanceof Layout) {
                ((Layout) node.actor).pack();
            }
            float y2 = y - node.getHeight();
            node.actor.setPosition(x, y2);
            y = y2 - ySpacing2;
            if (node.expanded) {
                y = layout(node.children, this.indentSpacing + indent, y, plusMinusWidth);
            }
        }
        return y;
    }

    public void draw(Batch batch, float parentAlpha) {
        drawBackground(batch, parentAlpha);
        Color color = getColor();
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
        draw(batch, this.rootNodes, this.paddingLeft, plusMinusWidth());
        super.draw(batch, parentAlpha);
    }

    /* access modifiers changed from: protected */
    public void drawBackground(Batch batch, float parentAlpha) {
        if (this.style.background != null) {
            Color color = getColor();
            batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
            this.style.background.draw(batch, getX(), getY(), getWidth(), getHeight());
        }
    }

    private void draw(Batch batch, Array<N> nodes, float indent, float plusMinusWidth) {
        float cullTop;
        float cullBottom;
        int i;
        int n;
        N node;
        float iconX;
        float height;
        float iconX2;
        Actor actor;
        Batch batch2 = batch;
        Array<N> array = nodes;
        float f = plusMinusWidth;
        Rectangle cullingArea = getCullingArea();
        if (cullingArea != null) {
            float cullBottom2 = cullingArea.y;
            cullBottom = cullBottom2;
            cullTop = cullBottom2 + cullingArea.height;
        } else {
            cullBottom = 0.0f;
            cullTop = 0.0f;
        }
        TreeStyle style2 = this.style;
        float x = getX();
        float y = getY();
        float expandX = x + indent;
        float iconX3 = expandX + f + this.iconSpacingLeft;
        int n2 = array.size;
        int i2 = 0;
        while (i2 < n2) {
            N node2 = (Node) array.get(i2);
            Actor actor2 = node2.actor;
            float actorY = actor2.getY();
            float height2 = node2.height;
            if (cullingArea == null || (actorY + height2 >= cullBottom && actorY <= cullTop)) {
                if (!this.selection.contains(node2) || style2.selection == null) {
                    height = height2;
                    actor = actor2;
                    node = node2;
                    i = i2;
                    n = n2;
                    iconX2 = iconX3;
                    if (node == this.overNode && style2.over != null) {
                        drawOver(node, style2.over, batch, x, (y + actorY) - (this.ySpacing / 2.0f), getWidth(), height + this.ySpacing);
                    }
                } else {
                    Drawable drawable = style2.selection;
                    float f2 = (y + actorY) - (this.ySpacing / 2.0f);
                    float width = getWidth();
                    float iconX4 = height2 + this.ySpacing;
                    height = height2;
                    Drawable drawable2 = drawable;
                    actor = actor2;
                    node = node2;
                    i = i2;
                    float f3 = f2;
                    n = n2;
                    float f4 = width;
                    iconX2 = iconX3;
                    drawSelection(node2, drawable2, batch, x, f3, f4, iconX4);
                }
                if (node.icon != null) {
                    float iconY = y + actorY + ((float) Math.round((height - node.icon.getMinHeight()) / 2.0f));
                    batch2.setColor(actor.getColor());
                    drawIcon(node, node.icon, batch, iconX2, iconY);
                    batch2.setColor(1.0f, 1.0f, 1.0f, 1.0f);
                }
                if (node.children.size > 0) {
                    iconX = iconX2;
                    Drawable expandIcon = getExpandIcon(node, iconX);
                    drawExpandIcon(node, expandIcon, batch, expandX, y + actorY + ((float) Math.round((height - expandIcon.getMinHeight()) / 2.0f)));
                } else {
                    iconX = iconX2;
                }
            } else if (actorY >= cullBottom) {
                float f5 = height2;
                Actor actor3 = actor2;
                node = node2;
                i = i2;
                n = n2;
                iconX = iconX3;
            } else {
                return;
            }
            if (node.expanded && node.children.size > 0) {
                draw(batch2, node.children, indent + this.indentSpacing, f);
            }
            i2 = i + 1;
            array = nodes;
            iconX3 = iconX;
            n2 = n;
        }
        int i3 = i2;
        int i4 = n2;
        float f6 = iconX3;
    }

    /* access modifiers changed from: protected */
    public void drawSelection(N n, Drawable selection2, Batch batch, float x, float y, float width, float height) {
        selection2.draw(batch, x, y, width, height);
    }

    /* access modifiers changed from: protected */
    public void drawOver(N n, Drawable over, Batch batch, float x, float y, float width, float height) {
        over.draw(batch, x, y, width, height);
    }

    /* access modifiers changed from: protected */
    public void drawExpandIcon(N n, Drawable expandIcon, Batch batch, float x, float y) {
        expandIcon.draw(batch, x, y, expandIcon.getMinWidth(), expandIcon.getMinHeight());
    }

    /* access modifiers changed from: protected */
    public void drawIcon(N n, Drawable icon, Batch batch, float x, float y) {
        icon.draw(batch, x, y, icon.getMinWidth(), icon.getMinHeight());
    }

    /* access modifiers changed from: protected */
    public Drawable getExpandIcon(N node, float iconX) {
        boolean over = false;
        if (node == this.overNode && Gdx.app.getType() == Application.ApplicationType.Desktop && (!this.selection.getMultiple() || (!UIUtils.ctrl() && !UIUtils.shift()))) {
            float mouseX = screenToLocalCoordinates(tmp.set((float) Gdx.input.getX(), 0.0f)).x;
            if (mouseX >= 0.0f && mouseX < iconX) {
                over = true;
            }
        }
        if (over) {
            Drawable icon = node.expanded ? this.style.minusOver : this.style.plusOver;
            if (icon != null) {
                return icon;
            }
        }
        return node.expanded ? this.style.minus : this.style.plus;
    }

    public N getNodeAt(float y) {
        this.foundNode = null;
        getNodeAt(this.rootNodes, y, getHeight());
        return this.foundNode;
    }

    private float getNodeAt(Array<N> nodes, float y, float rowY) {
        int i = 0;
        int n = nodes.size;
        while (i < n) {
            N node = (Node) nodes.get(i);
            float height = node.height;
            float rowY2 = rowY - (node.getHeight() - height);
            if (y < (rowY2 - height) - this.ySpacing || y >= rowY2) {
                rowY = rowY2 - (this.ySpacing + height);
                if (node.expanded) {
                    rowY = getNodeAt(node.children, y, rowY);
                    if (rowY == -1.0f) {
                        return -1.0f;
                    }
                }
                i++;
            } else {
                this.foundNode = node;
                return -1.0f;
            }
        }
        return rowY;
    }

    /* access modifiers changed from: package-private */
    public void selectNodes(Array<N> nodes, float low, float high) {
        int i = 0;
        int n = nodes.size;
        while (i < n) {
            N node = (Node) nodes.get(i);
            if (node.actor.getY() >= low) {
                if (node.isSelectable()) {
                    if (node.actor.getY() <= high) {
                        this.selection.add(node);
                    }
                    if (node.expanded) {
                        selectNodes(node.children, low, high);
                    }
                }
                i++;
            } else {
                return;
            }
        }
    }

    public Selection<N> getSelection() {
        return this.selection;
    }

    public N getSelectedNode() {
        return (Node) this.selection.first();
    }

    public V getSelectedValue() {
        N node = (Node) this.selection.first();
        if (node == null) {
            return null;
        }
        return node.getValue();
    }

    public TreeStyle getStyle() {
        return this.style;
    }

    public Array<N> getRootNodes() {
        return this.rootNodes;
    }

    public void updateRootNodes() {
        for (int i = this.rootNodes.size - 1; i >= 0; i--) {
            ((Node) this.rootNodes.get(i)).removeFromTree(this);
        }
        int n = this.rootNodes.size;
        for (int i2 = 0; i2 < n; i2++) {
            ((Node) this.rootNodes.get(i2)).addToTree(this);
        }
    }

    public N getOverNode() {
        return this.overNode;
    }

    public V getOverValue() {
        N n = this.overNode;
        if (n == null) {
            return null;
        }
        return n.getValue();
    }

    public void setOverNode(N overNode2) {
        this.overNode = overNode2;
    }

    public void setPadding(float padding) {
        this.paddingLeft = padding;
        this.paddingRight = padding;
    }

    public void setPadding(float left, float right) {
        this.paddingLeft = left;
        this.paddingRight = right;
    }

    public void setIndentSpacing(float indentSpacing2) {
        this.indentSpacing = indentSpacing2;
    }

    public float getIndentSpacing() {
        return this.indentSpacing;
    }

    public void setYSpacing(float ySpacing2) {
        this.ySpacing = ySpacing2;
    }

    public float getYSpacing() {
        return this.ySpacing;
    }

    public void setIconSpacing(float left, float right) {
        this.iconSpacingLeft = left;
        this.iconSpacingRight = right;
    }

    public float getPrefWidth() {
        if (this.sizeInvalid) {
            computeSize();
        }
        return this.prefWidth;
    }

    public float getPrefHeight() {
        if (this.sizeInvalid) {
            computeSize();
        }
        return this.prefHeight;
    }

    public void findExpandedValues(Array<V> values) {
        findExpandedValues(this.rootNodes, values);
    }

    public void restoreExpandedValues(Array<V> values) {
        int n = values.size;
        for (int i = 0; i < n; i++) {
            N node = findNode(values.get(i));
            if (node != null) {
                node.setExpanded(true);
                node.expandTo();
            }
        }
    }

    static boolean findExpandedValues(Array<? extends Node> nodes, Array values) {
        int n = nodes.size;
        for (int i = 0; i < n; i++) {
            Node node = (Node) nodes.get(i);
            if (node.expanded && !findExpandedValues(node.children, values)) {
                values.add(node.value);
            }
        }
        return false;
    }

    public N findNode(V value) {
        if (value != null) {
            return findNode(this.rootNodes, value);
        }
        throw new IllegalArgumentException("value cannot be null.");
    }

    static Node findNode(Array<? extends Node> nodes, Object value) {
        int n = nodes.size;
        for (int i = 0; i < n; i++) {
            Node node = (Node) nodes.get(i);
            if (value.equals(node.value)) {
                return node;
            }
        }
        int n2 = nodes.size;
        for (int i2 = 0; i2 < n2; i2++) {
            Node found = findNode(((Node) nodes.get(i2)).children, value);
            if (found != null) {
                return found;
            }
        }
        return null;
    }

    public void collapseAll() {
        collapseAll(this.rootNodes);
    }

    static void collapseAll(Array<? extends Node> nodes) {
        int n = nodes.size;
        for (int i = 0; i < n; i++) {
            Node node = (Node) nodes.get(i);
            node.setExpanded(false);
            collapseAll(node.children);
        }
    }

    public void expandAll() {
        expandAll(this.rootNodes);
    }

    static void expandAll(Array<? extends Node> nodes) {
        int n = nodes.size;
        for (int i = 0; i < n; i++) {
            ((Node) nodes.get(i)).expandAll();
        }
    }

    public ClickListener getClickListener() {
        return this.clickListener;
    }

    public static abstract class Node<N extends Node, V, A extends Actor> {
        A actor;
        final Array<N> children = new Array<>(0);
        boolean expanded;
        float height;
        Drawable icon;
        N parent;
        boolean selectable = true;
        V value;

        public Node(A actor2) {
            if (actor2 != null) {
                this.actor = actor2;
                return;
            }
            throw new IllegalArgumentException("actor cannot be null.");
        }

        public Node() {
        }

        public void setExpanded(boolean expanded2) {
            Tree tree;
            if (expanded2 != this.expanded) {
                this.expanded = expanded2;
                if (this.children.size != 0 && (tree = getTree()) != null) {
                    if (expanded2) {
                        int n = this.children.size;
                        for (int i = 0; i < n; i++) {
                            ((Node) this.children.get(i)).addToTree(tree);
                        }
                    } else {
                        for (int i2 = this.children.size - 1; i2 >= 0; i2--) {
                            ((Node) this.children.get(i2)).removeFromTree(tree);
                        }
                    }
                    tree.invalidateHierarchy();
                }
            }
        }

        /* access modifiers changed from: protected */
        public void addToTree(Tree<N, V> tree) {
            tree.addActor(this.actor);
            if (this.expanded) {
                Object[] children2 = this.children.items;
                for (int i = this.children.size - 1; i >= 0; i--) {
                    ((Node) children2[i]).addToTree(tree);
                }
            }
        }

        /* access modifiers changed from: protected */
        public void removeFromTree(Tree<N, V> tree) {
            tree.removeActor(this.actor);
            if (this.expanded) {
                Object[] children2 = this.children.items;
                for (int i = this.children.size - 1; i >= 0; i--) {
                    ((Node) children2[i]).removeFromTree(tree);
                }
            }
        }

        public void add(N node) {
            insert(this.children.size, node);
        }

        public void addAll(Array<N> nodes) {
            int n = nodes.size;
            for (int i = 0; i < n; i++) {
                insert(this.children.size, (Node) nodes.get(i));
            }
        }

        public void insert(int index, N node) {
            node.parent = this;
            this.children.insert(index, node);
            updateChildren();
        }

        public void remove() {
            Tree tree = getTree();
            if (tree != null) {
                tree.remove(this);
                return;
            }
            N n = this.parent;
            if (n != null) {
                n.remove(this);
            }
        }

        public void remove(N node) {
            Tree tree;
            this.children.removeValue(node, true);
            if (this.expanded && (tree = getTree()) != null) {
                node.removeFromTree(tree);
            }
        }

        public void removeAll() {
            Tree tree = getTree();
            if (tree != null) {
                Object[] children2 = this.children.items;
                for (int i = this.children.size - 1; i >= 0; i--) {
                    ((Node) children2[i]).removeFromTree(tree);
                }
            }
            this.children.clear();
        }

        public Tree<N, V> getTree() {
            Group parent2 = this.actor.getParent();
            if (parent2 instanceof Tree) {
                return (Tree) parent2;
            }
            return null;
        }

        public void setActor(A newActor) {
            Tree<N, V> tree;
            if (!(this.actor == null || (tree = getTree()) == null)) {
                this.actor.remove();
                tree.addActor(newActor);
            }
            this.actor = newActor;
        }

        public A getActor() {
            return this.actor;
        }

        public boolean isExpanded() {
            return this.expanded;
        }

        public Array<N> getChildren() {
            return this.children;
        }

        public boolean hasChildren() {
            return this.children.size > 0;
        }

        public void updateChildren() {
            Tree tree;
            if (this.expanded && (tree = getTree()) != null) {
                for (int i = this.children.size - 1; i >= 0; i--) {
                    ((Node) this.children.get(i)).removeFromTree(tree);
                }
                int n = this.children.size;
                for (int i2 = 0; i2 < n; i2++) {
                    ((Node) this.children.get(i2)).addToTree(tree);
                }
            }
        }

        public N getParent() {
            return this.parent;
        }

        public void setIcon(Drawable icon2) {
            this.icon = icon2;
        }

        public V getValue() {
            return this.value;
        }

        public void setValue(V value2) {
            this.value = value2;
        }

        public Drawable getIcon() {
            return this.icon;
        }

        public int getLevel() {
            int level = 0;
            Node current = this;
            do {
                level++;
                current = current.getParent();
            } while (current != null);
            return level;
        }

        public N findNode(V value2) {
            if (value2 == null) {
                throw new IllegalArgumentException("value cannot be null.");
            } else if (value2.equals(this.value)) {
                return this;
            } else {
                return Tree.findNode(this.children, value2);
            }
        }

        public void collapseAll() {
            setExpanded(false);
            Tree.collapseAll(this.children);
        }

        public void expandAll() {
            setExpanded(true);
            if (this.children.size > 0) {
                Tree.expandAll(this.children);
            }
        }

        public void expandTo() {
            for (Node node = this.parent; node != null; node = node.parent) {
                node.setExpanded(true);
            }
        }

        public boolean isSelectable() {
            return this.selectable;
        }

        public void setSelectable(boolean selectable2) {
            this.selectable = selectable2;
        }

        public void findExpandedValues(Array<V> values) {
            if (this.expanded && !Tree.findExpandedValues(this.children, values)) {
                values.add(this.value);
            }
        }

        public void restoreExpandedValues(Array<V> values) {
            int n = values.size;
            for (int i = 0; i < n; i++) {
                N node = findNode(values.get(i));
                if (node != null) {
                    node.setExpanded(true);
                    node.expandTo();
                }
            }
        }

        public float getHeight() {
            return this.height;
        }

        public boolean isAscendantOf(N node) {
            if (node != null) {
                Node current = node;
                while (current != this) {
                    current = current.parent;
                    if (current == null) {
                        return false;
                    }
                }
                return true;
            }
            throw new IllegalArgumentException("node cannot be null.");
        }

        public boolean isDescendantOf(N node) {
            if (node != null) {
                Node parent2 = this;
                while (parent2 != node) {
                    parent2 = parent2.parent;
                    if (parent2 == null) {
                        return false;
                    }
                }
                return true;
            }
            throw new IllegalArgumentException("node cannot be null.");
        }
    }

    public static class TreeStyle {
        public Drawable background;
        public Drawable minus;
        public Drawable minusOver;
        public Drawable over;
        public Drawable plus;
        public Drawable plusOver;
        public Drawable selection;

        public TreeStyle() {
        }

        public TreeStyle(Drawable plus2, Drawable minus2, Drawable selection2) {
            this.plus = plus2;
            this.minus = minus2;
            this.selection = selection2;
        }

        public TreeStyle(TreeStyle style) {
            this.plus = style.plus;
            this.minus = style.minus;
            this.plusOver = style.plusOver;
            this.minusOver = style.minusOver;
            this.over = style.over;
            this.selection = style.selection;
            this.background = style.background;
        }
    }
}
