/*
 * This file is part of NodeBox.
 *
 * Copyright (C) 2008 Frederik De Bleser (frederik@pandora.be)
 *
 * NodeBox is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * NodeBox is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with NodeBox. If not, see <http://www.gnu.org/licenses/>.
 */
package nodebox.graphics;

import java.awt.geom.Rectangle2D;
import java.util.*;

public class Rect implements Iterable<Float> {

    private float x, y, width, height;

    public Rect() {
        this(0, 0, 0, 0);
    }

    public Rect(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public Rect(Rect r) {
        this.x = r.x;
        this.y = r.y;
        this.width = r.width;
        this.height = r.height;
    }

    public Rect(java.awt.geom.Rectangle2D r) {
        this.x = (float) r.getX();
        this.y = (float) r.getY();
        this.width = (float) r.getWidth();
        this.height = (float) r.getHeight();
    }

    public static Rect centeredRect(float cx, float cy, float width, float height) {
        return new Rect(cx - width / 2, cy - height / 2, width, height);
    }

    public float getHeight() {
        return height;
    }

    public float getWidth() {
        return width;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public boolean isEmpty() {
        Rect n = normalized();
        return n.width <= 0 || n.height <= 0;
    }

    public Rect normalized() {
        Rect r = new Rect(this);
        if (r.width < 0) {
            r.x += r.width;
            r.width = -r.width;
        }
        if (r.height < 0) {
            r.y += r.height;
            r.height = -r.height;
        }
        return r;
    }

    public Rect united(Rect r) {
        Rect r1 = normalized();
        Rect r2 = r.normalized();
        Rect u = new Rect();
        u.x = Math.min(r1.x, r2.x);
        u.y = Math.min(r1.y, r2.y);
        u.width = Math.max(r1.x + r1.width, r2.x + r2.width) - u.x;
        u.height = Math.max(r1.y + r1.height, r2.y + r2.height) - u.y;
        return u;
    }

    public boolean intersects(Rect r) {
        Rect r1 = normalized();
        Rect r2 = r.normalized();
        return Math.max(r1.x, r1.y) < Math.min(r1.x + r1.width, r2.width) &&
                Math.max(r1.y, r2.y) < Math.min(r1.y + r1.height, r2.y + r2.height);
    }

    public boolean contains(Point p) {
        Rect r = normalized();
        return p.getX() >= r.x && p.getX() <= r.x + r.width &&
                p.getY() >= r.y && p.getY() <= r.y + r.height;
    }

    public boolean contains(Rect r) {
        Rect r1 = normalized();
        Rect r2 = r.normalized();
        return r2.x >= r1.x && r2.x + r2.width <= r1.x + r1.width &&
                r2.y >= r1.y && r2.y + r2.height <= r1.y + r1.height;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Rect)) return false;
        Rect r = (Rect) o;
        return x == r.x && y == r.y && width == r.width && height == r.height;
    }

    @Override
    public String toString() {
        return "Rect(" + x + ", " + y + ", " + width + ", " + height + ")";
    }

    public Rectangle2D getRectangle2D() {
        return new Rectangle2D.Float(x, y, width, height);
    }

    public Iterator<Float> iterator() {
        List<Float> list = new ArrayList<Float>();
        list.add(x);
        list.add(y);
        list.add(width);
        list.add(height);
        return list.iterator();
    }

    @Override
    public Rect clone() {
        return new Rect(this);
    }
}
