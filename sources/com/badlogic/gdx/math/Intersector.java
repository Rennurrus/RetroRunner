package com.badlogic.gdx.math;

import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;
import java.util.Arrays;
import java.util.List;

public final class Intersector {
    static Vector3 best = new Vector3();
    private static final Vector3 dir = new Vector3();
    private static final Vector2 e = new Vector2();
    private static final Vector2 ep1 = new Vector2();
    private static final Vector2 ep2 = new Vector2();
    private static final FloatArray floatArray = new FloatArray();
    private static final FloatArray floatArray2 = new FloatArray();
    private static final Vector3 i = new Vector3();
    static Vector3 intersection = new Vector3();
    private static final Vector2 ip = new Vector2();
    private static final Plane p = new Plane(new Vector3(), 0.0f);
    private static final Vector2 s = new Vector2();
    private static final Vector3 start = new Vector3();
    static Vector3 tmp = new Vector3();
    static Vector3 tmp1 = new Vector3();
    static Vector3 tmp2 = new Vector3();
    static Vector3 tmp3 = new Vector3();
    private static final Vector3 v0 = new Vector3();
    private static final Vector3 v1 = new Vector3();
    private static final Vector3 v2 = new Vector3();
    static Vector2 v2tmp = new Vector2();

    public static class MinimumTranslationVector {
        public float depth = 0.0f;
        public Vector2 normal = new Vector2();
    }

    public static boolean isPointInTriangle(Vector3 point, Vector3 t1, Vector3 t2, Vector3 t3) {
        v0.set(t1).sub(point);
        v1.set(t2).sub(point);
        v2.set(t3).sub(point);
        float ab = v0.dot(v1);
        float ac = v0.dot(v2);
        float bc = v1.dot(v2);
        Vector3 vector3 = v2;
        if ((bc * ac) - (vector3.dot(vector3) * ab) < 0.0f) {
            return false;
        }
        Vector3 vector32 = v1;
        if ((ab * bc) - (ac * vector32.dot(vector32)) < 0.0f) {
            return false;
        }
        return true;
    }

    public static boolean isPointInTriangle(Vector2 p2, Vector2 a, Vector2 b, Vector2 c) {
        float px1 = p2.x - a.x;
        float py1 = p2.y - a.y;
        boolean side12 = ((b.x - a.x) * py1) - ((b.y - a.y) * px1) > 0.0f;
        if ((((c.x - a.x) * py1) - ((c.y - a.y) * px1) > 0.0f) == side12) {
            return false;
        }
        return (((((c.x - b.x) * (p2.y - b.y)) - ((c.y - b.y) * (p2.x - b.x))) > 0.0f ? 1 : ((((c.x - b.x) * (p2.y - b.y)) - ((c.y - b.y) * (p2.x - b.x))) == 0.0f ? 0 : -1)) > 0) == side12;
    }

    public static boolean isPointInTriangle(float px, float py, float ax, float ay, float bx, float by, float cx, float cy) {
        float px1 = px - ax;
        float py1 = py - ay;
        boolean side12 = ((bx - ax) * py1) - ((by - ay) * px1) > 0.0f;
        if ((((cx - ax) * py1) - ((cy - ay) * px1) > 0.0f) == side12) {
            return false;
        }
        return (((((cx - bx) * (py - by)) - ((cy - by) * (px - bx))) > 0.0f ? 1 : ((((cx - bx) * (py - by)) - ((cy - by) * (px - bx))) == 0.0f ? 0 : -1)) > 0) == side12;
    }

    public static boolean intersectSegmentPlane(Vector3 start2, Vector3 end, Plane plane, Vector3 intersection2) {
        Vector3 dir2 = v0.set(end).sub(start2);
        float denom = dir2.dot(plane.getNormal());
        if (denom == 0.0f) {
            return false;
        }
        float t = (-(start2.dot(plane.getNormal()) + plane.getD())) / denom;
        if (t < 0.0f || t > 1.0f) {
            return false;
        }
        intersection2.set(start2).add(dir2.scl(t));
        return true;
    }

    public static int pointLineSide(Vector2 linePoint1, Vector2 linePoint2, Vector2 point) {
        return (int) Math.signum(((linePoint2.x - linePoint1.x) * (point.y - linePoint1.y)) - ((linePoint2.y - linePoint1.y) * (point.x - linePoint1.x)));
    }

    public static int pointLineSide(float linePoint1X, float linePoint1Y, float linePoint2X, float linePoint2Y, float pointX, float pointY) {
        return (int) Math.signum(((linePoint2X - linePoint1X) * (pointY - linePoint1Y)) - ((linePoint2Y - linePoint1Y) * (pointX - linePoint1X)));
    }

    public static boolean isPointInPolygon(Array<Vector2> polygon, Vector2 point) {
        Vector2 last = polygon.peek();
        float x = point.x;
        float y = point.y;
        boolean oddNodes = false;
        for (int i2 = 0; i2 < polygon.size; i2++) {
            Vector2 vertex = polygon.get(i2);
            if (((vertex.y < y && last.y >= y) || (last.y < y && vertex.y >= y)) && vertex.x + (((y - vertex.y) / (last.y - vertex.y)) * (last.x - vertex.x)) < x) {
                oddNodes = !oddNodes;
            }
            last = vertex;
        }
        return oddNodes;
    }

    public static boolean isPointInPolygon(float[] polygon, int offset, int count, float x, float y) {
        boolean oddNodes;
        boolean oddNodes2 = false;
        float sx = polygon[offset];
        float sy = polygon[offset + 1];
        float y1 = sy;
        int yi = offset + 3;
        int n = offset + count;
        while (true) {
            oddNodes = false;
            if (yi >= n) {
                break;
            }
            float y2 = polygon[yi];
            if ((y2 < y && y1 >= y) || (y1 < y && y2 >= y)) {
                float x2 = polygon[yi - 1];
                if ((((y - y2) / (y1 - y2)) * (polygon[yi - 3] - x2)) + x2 < x) {
                    if (!oddNodes2) {
                        oddNodes = true;
                    }
                    oddNodes2 = oddNodes;
                }
            }
            y1 = y2;
            yi += 2;
        }
        if (((sy >= y || y1 < y) && (y1 >= y || sy < y)) || (((y - sy) / (y1 - sy)) * (polygon[yi - 3] - sx)) + sx >= x) {
            return oddNodes2;
        }
        if (!oddNodes2) {
            oddNodes = true;
        }
        return oddNodes;
    }

    /* JADX WARNING: type inference failed for: r2v0 */
    /* JADX WARNING: type inference failed for: r2v1, types: [boolean] */
    /* JADX WARNING: type inference failed for: r2v7 */
    public static boolean intersectPolygons(Polygon p1, Polygon p2, Polygon overlap) {
        Polygon polygon = overlap;
        ? r2 = 0;
        if (p1.getVertices().length == 0 || p2.getVertices().length == 0) {
            return false;
        }
        Vector2 ip2 = ip;
        Vector2 ep12 = ep1;
        Vector2 ep22 = ep2;
        Vector2 s2 = s;
        Vector2 e2 = e;
        FloatArray floatArray3 = floatArray;
        FloatArray floatArray22 = floatArray2;
        floatArray3.clear();
        floatArray22.clear();
        floatArray22.addAll(p1.getTransformedVertices());
        float[] vertices2 = p2.getTransformedVertices();
        int i2 = 0;
        int i3 = 2;
        int last = vertices2.length - 2;
        while (i2 <= last) {
            ep12.set(vertices2[i2], vertices2[i2 + 1]);
            if (i2 < last) {
                ep22.set(vertices2[i2 + 2], vertices2[i2 + 3]);
            } else {
                ep22.set(vertices2[r2], vertices2[1]);
            }
            if (floatArray22.size == 0) {
                return r2;
            }
            s2.set(floatArray22.get(floatArray22.size - i3), floatArray22.get(floatArray22.size - 1));
            int j = 0;
            while (j < floatArray22.size) {
                e2.set(floatArray22.get(j), floatArray22.get(j + 1));
                boolean side = pointLineSide(ep22, ep12, s2) > 0;
                if (pointLineSide(ep22, ep12, e2) > 0) {
                    if (!side) {
                        intersectLines(s2, e2, ep12, ep22, ip2);
                        if (!(floatArray3.size >= i3 && floatArray3.get(floatArray3.size - i3) == ip2.x && floatArray3.get(floatArray3.size - 1) == ip2.y)) {
                            floatArray3.add(ip2.x);
                            floatArray3.add(ip2.y);
                        }
                    }
                    floatArray3.add(e2.x);
                    floatArray3.add(e2.y);
                } else if (side) {
                    intersectLines(s2, e2, ep12, ep22, ip2);
                    floatArray3.add(ip2.x);
                    floatArray3.add(ip2.y);
                }
                s2.set(e2.x, e2.y);
                j += 2;
                i3 = 2;
            }
            floatArray22.clear();
            floatArray22.addAll(floatArray3);
            floatArray3.clear();
            i2 += 2;
            r2 = 0;
            i3 = 2;
        }
        if (floatArray22.size == 0) {
            return false;
        }
        if (polygon != null) {
            if (overlap.getVertices().length == floatArray22.size) {
                System.arraycopy(floatArray22.items, 0, overlap.getVertices(), 0, floatArray22.size);
            } else {
                polygon.setVertices(floatArray22.toArray());
            }
        }
        return true;
    }

    public static boolean intersectPolygons(FloatArray polygon1, FloatArray polygon2) {
        if (!isPointInPolygon(polygon1.items, 0, polygon1.size, polygon2.items[0], polygon2.items[1]) && !isPointInPolygon(polygon2.items, 0, polygon2.size, polygon1.items[0], polygon1.items[1])) {
            return intersectPolygonEdges(polygon1, polygon2);
        }
        return true;
    }

    public static boolean intersectPolygonEdges(FloatArray polygon1, FloatArray polygon2) {
        FloatArray floatArray3 = polygon1;
        FloatArray floatArray4 = polygon2;
        int last1 = floatArray3.size - 2;
        int last2 = floatArray4.size - 2;
        float[] p1 = floatArray3.items;
        float[] p2 = floatArray4.items;
        float x1 = p1[last1];
        float y1 = p1[last1 + 1];
        for (int i2 = 0; i2 <= last1; i2 += 2) {
            float x2 = p1[i2];
            float y2 = p1[i2 + 1];
            float x3 = p2[last2];
            float y3 = p2[last2 + 1];
            int j = 0;
            while (j <= last2) {
                float x4 = p2[j];
                float y4 = p2[j + 1];
                int j2 = j;
                if (intersectSegments(x1, y1, x2, y2, x3, y3, x4, y4, (Vector2) null)) {
                    return true;
                }
                x3 = x4;
                y3 = y4;
                j = j2 + 2;
            }
            int i3 = j;
            x1 = x2;
            y1 = y2;
        }
        return false;
    }

    public static float distanceLinePoint(float startX, float startY, float endX, float endY, float pointX, float pointY) {
        return Math.abs(((pointX - startX) * (endY - startY)) - ((pointY - startY) * (endX - startX))) / ((float) Math.sqrt((double) (((endX - startX) * (endX - startX)) + ((endY - startY) * (endY - startY)))));
    }

    public static float distanceSegmentPoint(float startX, float startY, float endX, float endY, float pointX, float pointY) {
        return nearestSegmentPoint(startX, startY, endX, endY, pointX, pointY, v2tmp).dst(pointX, pointY);
    }

    public static float distanceSegmentPoint(Vector2 start2, Vector2 end, Vector2 point) {
        return nearestSegmentPoint(start2, end, point, v2tmp).dst(point);
    }

    public static Vector2 nearestSegmentPoint(Vector2 start2, Vector2 end, Vector2 point, Vector2 nearest) {
        float length2 = start2.dst2(end);
        if (length2 == 0.0f) {
            return nearest.set(start2);
        }
        float t = (((point.x - start2.x) * (end.x - start2.x)) + ((point.y - start2.y) * (end.y - start2.y))) / length2;
        if (t < 0.0f) {
            return nearest.set(start2);
        }
        if (t > 1.0f) {
            return nearest.set(end);
        }
        return nearest.set(start2.x + ((end.x - start2.x) * t), start2.y + ((end.y - start2.y) * t));
    }

    public static Vector2 nearestSegmentPoint(float startX, float startY, float endX, float endY, float pointX, float pointY, Vector2 nearest) {
        float xDiff = endX - startX;
        float yDiff = endY - startY;
        float length2 = (xDiff * xDiff) + (yDiff * yDiff);
        if (length2 == 0.0f) {
            return nearest.set(startX, startY);
        }
        float t = (((pointX - startX) * (endX - startX)) + ((pointY - startY) * (endY - startY))) / length2;
        if (t < 0.0f) {
            return nearest.set(startX, startY);
        }
        if (t > 1.0f) {
            return nearest.set(endX, endY);
        }
        return nearest.set(((endX - startX) * t) + startX, ((endY - startY) * t) + startY);
    }

    public static boolean intersectSegmentCircle(Vector2 start2, Vector2 end, Vector2 center, float squareRadius) {
        tmp.set(end.x - start2.x, end.y - start2.y, 0.0f);
        tmp1.set(center.x - start2.x, center.y - start2.y, 0.0f);
        float l = tmp.len();
        float u = tmp1.dot(tmp.nor());
        if (u <= 0.0f) {
            tmp2.set(start2.x, start2.y, 0.0f);
        } else if (u >= l) {
            tmp2.set(end.x, end.y, 0.0f);
        } else {
            tmp3.set(tmp.scl(u));
            tmp2.set(tmp3.x + start2.x, tmp3.y + start2.y, 0.0f);
        }
        float x = center.x - tmp2.x;
        float y = center.y - tmp2.y;
        return (x * x) + (y * y) <= squareRadius;
    }

    public static float intersectSegmentCircleDisplace(Vector2 start2, Vector2 end, Vector2 point, float radius, Vector2 displacement) {
        float u = ((point.x - start2.x) * (end.x - start2.x)) + ((point.y - start2.y) * (end.y - start2.y));
        float d = start2.dst(end);
        float u2 = u / (d * d);
        if (u2 < 0.0f || u2 > 1.0f) {
            return Float.POSITIVE_INFINITY;
        }
        tmp.set(end.x, end.y, 0.0f).sub(start2.x, start2.y, 0.0f);
        tmp2.set(start2.x, start2.y, 0.0f).add(tmp.scl(u2));
        float d2 = tmp2.dst(point.x, point.y, 0.0f);
        if (d2 >= radius) {
            return Float.POSITIVE_INFINITY;
        }
        displacement.set(point).sub(tmp2.x, tmp2.y).nor();
        return d2;
    }

    public static float intersectRayRay(Vector2 start1, Vector2 direction1, Vector2 start2, Vector2 direction2) {
        float difx = start2.x - start1.x;
        float dify = start2.y - start1.y;
        float d1xd2 = (direction1.x * direction2.y) - (direction1.y * direction2.x);
        if (d1xd2 == 0.0f) {
            return Float.POSITIVE_INFINITY;
        }
        return (difx * (direction2.y / d1xd2)) - (dify * (direction2.x / d1xd2));
    }

    public static boolean intersectRayPlane(Ray ray, Plane plane, Vector3 intersection2) {
        float denom = ray.direction.dot(plane.getNormal());
        if (denom != 0.0f) {
            float t = (-(ray.origin.dot(plane.getNormal()) + plane.getD())) / denom;
            if (t < 0.0f) {
                return false;
            }
            if (intersection2 != null) {
                intersection2.set(ray.origin).add(v0.set(ray.direction).scl(t));
            }
            return true;
        } else if (plane.testPoint(ray.origin) != Plane.PlaneSide.OnPlane) {
            return false;
        } else {
            if (intersection2 != null) {
                intersection2.set(ray.origin);
            }
            return true;
        }
    }

    public static float intersectLinePlane(float x, float y, float z, float x2, float y2, float z2, Plane plane, Vector3 intersection2) {
        Vector3 direction = tmp.set(x2, y2, z2).sub(x, y, z);
        Vector3 origin = tmp2.set(x, y, z);
        float denom = direction.dot(plane.getNormal());
        if (denom != 0.0f) {
            float t = (-(origin.dot(plane.getNormal()) + plane.getD())) / denom;
            if (intersection2 != null) {
                intersection2.set(origin).add(direction.scl(t));
            }
            return t;
        } else if (plane.testPoint(origin) != Plane.PlaneSide.OnPlane) {
            return -1.0f;
        } else {
            if (intersection2 != null) {
                intersection2.set(origin);
            }
            return 0.0f;
        }
    }

    public static boolean intersectRayTriangle(Ray ray, Vector3 t1, Vector3 t2, Vector3 t3, Vector3 intersection2) {
        Ray ray2 = ray;
        Vector3 vector3 = t1;
        Vector3 vector32 = t2;
        Vector3 vector33 = t3;
        Vector3 vector34 = intersection2;
        Vector3 edge1 = v0.set(vector32).sub(vector3);
        Vector3 edge2 = v1.set(vector33).sub(vector3);
        Vector3 pvec = v2.set(ray2.direction).crs(edge2);
        float det = edge1.dot(pvec);
        if (MathUtils.isZero(det)) {
            p.set(vector3, vector32, vector33);
            if (p.testPoint(ray2.origin) != Plane.PlaneSide.OnPlane || !isPointInTriangle(ray2.origin, vector3, vector32, vector33)) {
                return false;
            }
            if (vector34 != null) {
                vector34.set(ray2.origin);
            }
            return true;
        }
        float det2 = 1.0f / det;
        Vector3 tvec = i.set(ray2.origin).sub(vector3);
        float u = tvec.dot(pvec) * det2;
        if (u < 0.0f || u > 1.0f) {
            return false;
        }
        Vector3 qvec = tvec.crs(edge1);
        float v = ray2.direction.dot(qvec) * det2;
        if (v < 0.0f || u + v > 1.0f) {
            return false;
        }
        float t = edge2.dot(qvec) * det2;
        if (t < 0.0f) {
            return false;
        }
        if (vector34 == null) {
            return true;
        }
        if (t <= 1.0E-6f) {
            vector34.set(ray2.origin);
            return true;
        }
        ray2.getEndPoint(vector34, t);
        return true;
    }

    public static boolean intersectRaySphere(Ray ray, Vector3 center, float radius, Vector3 intersection2) {
        float len = ray.direction.dot(center.x - ray.origin.x, center.y - ray.origin.y, center.z - ray.origin.z);
        if (len < 0.0f) {
            return false;
        }
        float dst2 = center.dst2(ray.origin.x + (ray.direction.x * len), ray.origin.y + (ray.direction.y * len), ray.origin.z + (ray.direction.z * len));
        float r2 = radius * radius;
        if (dst2 > r2) {
            return false;
        }
        if (intersection2 == null) {
            return true;
        }
        intersection2.set(ray.direction).scl(len - ((float) Math.sqrt((double) (r2 - dst2)))).add(ray.origin);
        return true;
    }

    public static boolean intersectRayBounds(Ray ray, BoundingBox box, Vector3 intersection2) {
        if (!box.contains(ray.origin)) {
            float lowest = 0.0f;
            boolean hit = false;
            if (ray.origin.x <= box.min.x && ray.direction.x > 0.0f) {
                float t = (box.min.x - ray.origin.x) / ray.direction.x;
                if (t >= 0.0f) {
                    v2.set(ray.direction).scl(t).add(ray.origin);
                    if (v2.y >= box.min.y && v2.y <= box.max.y && v2.z >= box.min.z && v2.z <= box.max.z && (0 == 0 || t < 0.0f)) {
                        hit = true;
                        lowest = t;
                    }
                }
            }
            if (ray.origin.x >= box.max.x && ray.direction.x < 0.0f) {
                float t2 = (box.max.x - ray.origin.x) / ray.direction.x;
                if (t2 >= 0.0f) {
                    v2.set(ray.direction).scl(t2).add(ray.origin);
                    if (v2.y >= box.min.y && v2.y <= box.max.y && v2.z >= box.min.z && v2.z <= box.max.z && (!hit || t2 < lowest)) {
                        hit = true;
                        lowest = t2;
                    }
                }
            }
            if (ray.origin.y <= box.min.y && ray.direction.y > 0.0f) {
                float t3 = (box.min.y - ray.origin.y) / ray.direction.y;
                if (t3 >= 0.0f) {
                    v2.set(ray.direction).scl(t3).add(ray.origin);
                    if (v2.x >= box.min.x && v2.x <= box.max.x && v2.z >= box.min.z && v2.z <= box.max.z && (!hit || t3 < lowest)) {
                        hit = true;
                        lowest = t3;
                    }
                }
            }
            if (ray.origin.y >= box.max.y && ray.direction.y < 0.0f) {
                float t4 = (box.max.y - ray.origin.y) / ray.direction.y;
                if (t4 >= 0.0f) {
                    v2.set(ray.direction).scl(t4).add(ray.origin);
                    if (v2.x >= box.min.x && v2.x <= box.max.x && v2.z >= box.min.z && v2.z <= box.max.z && (!hit || t4 < lowest)) {
                        hit = true;
                        lowest = t4;
                    }
                }
            }
            if (ray.origin.z <= box.min.z && ray.direction.z > 0.0f) {
                float t5 = (box.min.z - ray.origin.z) / ray.direction.z;
                if (t5 >= 0.0f) {
                    v2.set(ray.direction).scl(t5).add(ray.origin);
                    if (v2.x >= box.min.x && v2.x <= box.max.x && v2.y >= box.min.y && v2.y <= box.max.y && (!hit || t5 < lowest)) {
                        hit = true;
                        lowest = t5;
                    }
                }
            }
            if (ray.origin.z >= box.max.z && ray.direction.z < 0.0f) {
                float t6 = (box.max.z - ray.origin.z) / ray.direction.z;
                if (t6 >= 0.0f) {
                    v2.set(ray.direction).scl(t6).add(ray.origin);
                    if (v2.x >= box.min.x && v2.x <= box.max.x && v2.y >= box.min.y && v2.y <= box.max.y && (!hit || t6 < lowest)) {
                        hit = true;
                        lowest = t6;
                    }
                }
            }
            if (hit && intersection2 != null) {
                intersection2.set(ray.direction).scl(lowest).add(ray.origin);
                if (intersection2.x < box.min.x) {
                    intersection2.x = box.min.x;
                } else if (intersection2.x > box.max.x) {
                    intersection2.x = box.max.x;
                }
                if (intersection2.y < box.min.y) {
                    intersection2.y = box.min.y;
                } else if (intersection2.y > box.max.y) {
                    intersection2.y = box.max.y;
                }
                if (intersection2.z < box.min.z) {
                    intersection2.z = box.min.z;
                } else if (intersection2.z > box.max.z) {
                    intersection2.z = box.max.z;
                }
            }
            return hit;
        } else if (intersection2 == null) {
            return true;
        } else {
            intersection2.set(ray.origin);
            return true;
        }
    }

    public static boolean intersectRayBoundsFast(Ray ray, BoundingBox box) {
        return intersectRayBoundsFast(ray, box.getCenter(tmp1), box.getDimensions(tmp2));
    }

    public static boolean intersectRayBoundsFast(Ray ray, Vector3 center, Vector3 dimensions) {
        float divX = 1.0f / ray.direction.x;
        float divY = 1.0f / ray.direction.y;
        float divZ = 1.0f / ray.direction.z;
        float minx = ((center.x - (dimensions.x * 0.5f)) - ray.origin.x) * divX;
        float maxx = ((center.x + (dimensions.x * 0.5f)) - ray.origin.x) * divX;
        if (minx > maxx) {
            float t = minx;
            minx = maxx;
            maxx = t;
        }
        float miny = ((center.y - (dimensions.y * 0.5f)) - ray.origin.y) * divY;
        float maxy = ((center.y + (dimensions.y * 0.5f)) - ray.origin.y) * divY;
        if (miny > maxy) {
            float t2 = miny;
            miny = maxy;
            maxy = t2;
        }
        float minz = ((center.z - (dimensions.z * 0.5f)) - ray.origin.z) * divZ;
        float maxz = ((center.z + (dimensions.z * 0.5f)) - ray.origin.z) * divZ;
        if (minz > maxz) {
            float t3 = minz;
            minz = maxz;
            maxz = t3;
        }
        float min = Math.max(Math.max(minx, miny), minz);
        float max = Math.min(Math.min(maxx, maxy), maxz);
        return max >= 0.0f && max >= min;
    }

    public static boolean intersectRayTriangles(Ray ray, float[] triangles, Vector3 intersection2) {
        float min_dist = Float.MAX_VALUE;
        boolean hit = false;
        if (triangles.length % 9 == 0) {
            for (int i2 = 0; i2 < triangles.length; i2 += 9) {
                if (intersectRayTriangle(ray, tmp1.set(triangles[i2], triangles[i2 + 1], triangles[i2 + 2]), tmp2.set(triangles[i2 + 3], triangles[i2 + 4], triangles[i2 + 5]), tmp3.set(triangles[i2 + 6], triangles[i2 + 7], triangles[i2 + 8]), tmp)) {
                    float dist = ray.origin.dst2(tmp);
                    if (dist < min_dist) {
                        min_dist = dist;
                        best.set(tmp);
                        hit = true;
                    }
                }
            }
            if (!hit) {
                return false;
            }
            if (intersection2 == null) {
                return true;
            }
            intersection2.set(best);
            return true;
        }
        throw new RuntimeException("triangles array size is not a multiple of 9");
    }

    public static boolean intersectRayTriangles(Ray ray, float[] vertices, short[] indices, int vertexSize, Vector3 intersection2) {
        Ray ray2 = ray;
        short[] sArr = indices;
        Vector3 vector3 = intersection2;
        float min_dist = Float.MAX_VALUE;
        boolean hit = false;
        if (sArr.length % 3 == 0) {
            for (int i2 = 0; i2 < sArr.length; i2 += 3) {
                int i1 = sArr[i2] * vertexSize;
                int i22 = sArr[i2 + 1] * vertexSize;
                int i3 = sArr[i2 + 2] * vertexSize;
                if (intersectRayTriangle(ray, tmp1.set(vertices[i1], vertices[i1 + 1], vertices[i1 + 2]), tmp2.set(vertices[i22], vertices[i22 + 1], vertices[i22 + 2]), tmp3.set(vertices[i3], vertices[i3 + 1], vertices[i3 + 2]), tmp)) {
                    float dist = ray2.origin.dst2(tmp);
                    if (dist < min_dist) {
                        min_dist = dist;
                        best.set(tmp);
                        hit = true;
                    }
                }
            }
            if (!hit) {
                return false;
            }
            if (vector3 == null) {
                return true;
            }
            vector3.set(best);
            return true;
        }
        throw new RuntimeException("triangle list size is not a multiple of 3");
    }

    public static boolean intersectRayTriangles(Ray ray, List<Vector3> triangles, Vector3 intersection2) {
        float min_dist = Float.MAX_VALUE;
        boolean hit = false;
        if (triangles.size() % 3 == 0) {
            for (int i2 = 0; i2 < triangles.size(); i2 += 3) {
                if (intersectRayTriangle(ray, triangles.get(i2), triangles.get(i2 + 1), triangles.get(i2 + 2), tmp)) {
                    float dist = ray.origin.dst2(tmp);
                    if (dist < min_dist) {
                        min_dist = dist;
                        best.set(tmp);
                        hit = true;
                    }
                }
            }
            if (!hit) {
                return false;
            }
            if (intersection2 == null) {
                return true;
            }
            intersection2.set(best);
            return true;
        }
        throw new RuntimeException("triangle list size is not a multiple of 3");
    }

    public static boolean intersectLines(Vector2 p1, Vector2 p2, Vector2 p3, Vector2 p4, Vector2 intersection2) {
        Vector2 vector2 = p1;
        Vector2 vector22 = p2;
        Vector2 vector23 = p3;
        Vector2 vector24 = p4;
        Vector2 vector25 = intersection2;
        float x1 = vector2.x;
        float y1 = vector2.y;
        float x2 = vector22.x;
        float y2 = vector22.y;
        float x3 = vector23.x;
        float y3 = vector23.y;
        float x4 = vector24.x;
        float y4 = vector24.y;
        float d = ((y4 - y3) * (x2 - x1)) - ((x4 - x3) * (y2 - y1));
        if (d == 0.0f) {
            return false;
        }
        if (vector25 == null) {
            return true;
        }
        float ua = (((x4 - x3) * (y1 - y3)) - ((y4 - y3) * (x1 - x3))) / d;
        vector25.set(((x2 - x1) * ua) + x1, y1 + ((y2 - y1) * ua));
        return true;
    }

    public static boolean intersectLines(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4, Vector2 intersection2) {
        float d = ((y4 - y3) * (x2 - x1)) - ((x4 - x3) * (y2 - y1));
        if (d == 0.0f) {
            return false;
        }
        if (intersection2 == null) {
            return true;
        }
        float ua = (((x4 - x3) * (y1 - y3)) - ((y4 - y3) * (x1 - x3))) / d;
        intersection2.set(((x2 - x1) * ua) + x1, ((y2 - y1) * ua) + y1);
        return true;
    }

    public static boolean intersectLinePolygon(Vector2 p1, Vector2 p2, Polygon polygon) {
        Vector2 vector2 = p1;
        Vector2 vector22 = p2;
        float[] vertices = polygon.getTransformedVertices();
        float x1 = vector2.x;
        float y1 = vector2.y;
        float x2 = vector22.x;
        float y2 = vector22.y;
        int n = vertices.length;
        float x3 = vertices[n - 2];
        float y3 = vertices[n - 1];
        for (int i2 = 0; i2 < n; i2 += 2) {
            float x4 = vertices[i2];
            float y4 = vertices[i2 + 1];
            float d = ((y4 - y3) * (x2 - x1)) - ((x4 - x3) * (y2 - y1));
            if (d != 0.0f) {
                float ua = (((x4 - x3) * (y1 - y3)) - ((y4 - y3) * (x1 - x3))) / d;
                if (ua >= 0.0f && ua <= 1.0f) {
                    return true;
                }
            }
            x3 = x4;
            y3 = y4;
        }
        return false;
    }

    public static boolean intersectRectangles(Rectangle rectangle1, Rectangle rectangle2, Rectangle intersection2) {
        if (!rectangle1.overlaps(rectangle2)) {
            return false;
        }
        intersection2.x = Math.max(rectangle1.x, rectangle2.x);
        intersection2.width = Math.min(rectangle1.x + rectangle1.width, rectangle2.x + rectangle2.width) - intersection2.x;
        intersection2.y = Math.max(rectangle1.y, rectangle2.y);
        intersection2.height = Math.min(rectangle1.y + rectangle1.height, rectangle2.y + rectangle2.height) - intersection2.y;
        return true;
    }

    public static boolean intersectSegmentRectangle(float startX, float startY, float endX, float endY, Rectangle rectangle) {
        Rectangle rectangle2 = rectangle;
        float rectangleEndX = rectangle2.x + rectangle2.width;
        float rectangleEndY = rectangle2.y + rectangle2.height;
        if (intersectSegments(startX, startY, endX, endY, rectangle2.x, rectangle2.y, rectangle2.x, rectangleEndY, (Vector2) null)) {
            return true;
        }
        if (intersectSegments(startX, startY, endX, endY, rectangle2.x, rectangle2.y, rectangleEndX, rectangle2.y, (Vector2) null)) {
            return true;
        }
        if (intersectSegments(startX, startY, endX, endY, rectangleEndX, rectangle2.y, rectangleEndX, rectangleEndY, (Vector2) null)) {
            return true;
        }
        if (intersectSegments(startX, startY, endX, endY, rectangle2.x, rectangleEndY, rectangleEndX, rectangleEndY, (Vector2) null)) {
            return true;
        }
        float f = startX;
        float f2 = startY;
        return rectangle2.contains(startX, startY);
    }

    public static boolean intersectSegmentRectangle(Vector2 start2, Vector2 end, Rectangle rectangle) {
        return intersectSegmentRectangle(start2.x, start2.y, end.x, end.y, rectangle);
    }

    public static boolean intersectSegmentPolygon(Vector2 p1, Vector2 p2, Polygon polygon) {
        Vector2 vector2 = p1;
        Vector2 vector22 = p2;
        float[] vertices = polygon.getTransformedVertices();
        float x1 = vector2.x;
        float y1 = vector2.y;
        float x2 = vector22.x;
        float y2 = vector22.y;
        int n = vertices.length;
        float x3 = vertices[n - 2];
        float y3 = vertices[n - 1];
        for (int i2 = 0; i2 < n; i2 += 2) {
            float x4 = vertices[i2];
            float y4 = vertices[i2 + 1];
            float d = ((y4 - y3) * (x2 - x1)) - ((x4 - x3) * (y2 - y1));
            if (d != 0.0f) {
                float yd = y1 - y3;
                float xd = x1 - x3;
                float ua = (((x4 - x3) * yd) - ((y4 - y3) * xd)) / d;
                if (ua >= 0.0f && ua <= 1.0f) {
                    float ub = (((x2 - x1) * yd) - ((y2 - y1) * xd)) / d;
                    if (ub >= 0.0f && ub <= 1.0f) {
                        return true;
                    }
                }
            }
            x3 = x4;
            y3 = y4;
        }
        return false;
    }

    public static boolean intersectSegments(Vector2 p1, Vector2 p2, Vector2 p3, Vector2 p4, Vector2 intersection2) {
        Vector2 vector2 = p1;
        Vector2 vector22 = p2;
        Vector2 vector23 = p3;
        Vector2 vector24 = p4;
        Vector2 vector25 = intersection2;
        float x1 = vector2.x;
        float y1 = vector2.y;
        float x2 = vector22.x;
        float y2 = vector22.y;
        float x3 = vector23.x;
        float y3 = vector23.y;
        float x4 = vector24.x;
        float y4 = vector24.y;
        float d = ((y4 - y3) * (x2 - x1)) - ((x4 - x3) * (y2 - y1));
        if (d == 0.0f) {
            return false;
        }
        float yd = y1 - y3;
        float xd = x1 - x3;
        float ua = (((x4 - x3) * yd) - ((y4 - y3) * xd)) / d;
        if (ua < 0.0f || ua > 1.0f) {
            return false;
        }
        float ub = (((x2 - x1) * yd) - ((y2 - y1) * xd)) / d;
        if (ub < 0.0f || ub > 1.0f) {
            return false;
        }
        if (vector25 == null) {
            return true;
        }
        vector25.set(((x2 - x1) * ua) + x1, ((y2 - y1) * ua) + y1);
        return true;
    }

    public static boolean intersectSegments(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4, Vector2 intersection2) {
        Vector2 vector2 = intersection2;
        float d = ((y4 - y3) * (x2 - x1)) - ((x4 - x3) * (y2 - y1));
        if (d == 0.0f) {
            return false;
        }
        float yd = y1 - y3;
        float xd = x1 - x3;
        float ua = (((x4 - x3) * yd) - ((y4 - y3) * xd)) / d;
        if (ua < 0.0f || ua > 1.0f) {
            return false;
        }
        float ub = (((x2 - x1) * yd) - ((y2 - y1) * xd)) / d;
        if (ub < 0.0f || ub > 1.0f) {
            return false;
        }
        if (vector2 == null) {
            return true;
        }
        vector2.set(((x2 - x1) * ua) + x1, ((y2 - y1) * ua) + y1);
        return true;
    }

    static float det(float a, float b, float c, float d) {
        return (a * d) - (b * c);
    }

    static double detd(double a, double b, double c, double d) {
        return (a * d) - (b * c);
    }

    public static boolean overlaps(Circle c1, Circle c2) {
        return c1.overlaps(c2);
    }

    public static boolean overlaps(Rectangle r1, Rectangle r2) {
        return r1.overlaps(r2);
    }

    public static boolean overlaps(Circle c, Rectangle r) {
        float closestX = c.x;
        float closestY = c.y;
        if (c.x < r.x) {
            closestX = r.x;
        } else if (c.x > r.x + r.width) {
            closestX = r.x + r.width;
        }
        if (c.y < r.y) {
            closestY = r.y;
        } else if (c.y > r.y + r.height) {
            closestY = r.y + r.height;
        }
        float closestX2 = closestX - c.x;
        float closestY2 = closestY - c.y;
        return (closestX2 * closestX2) + (closestY2 * closestY2) < c.radius * c.radius;
    }

    public static boolean overlapConvexPolygons(Polygon p1, Polygon p2) {
        return overlapConvexPolygons(p1, p2, (MinimumTranslationVector) null);
    }

    public static boolean overlapConvexPolygons(Polygon p1, Polygon p2, MinimumTranslationVector mtv) {
        return overlapConvexPolygons(p1.getTransformedVertices(), p2.getTransformedVertices(), mtv);
    }

    public static boolean overlapConvexPolygons(float[] verts1, float[] verts2, MinimumTranslationVector mtv) {
        return overlapConvexPolygons(verts1, 0, verts1.length, verts2, 0, verts2.length, mtv);
    }

    public static boolean overlapConvexPolygons(float[] verts1, int offset1, int count1, float[] verts2, int offset2, int count2, MinimumTranslationVector mtv) {
        int end1;
        MinimumTranslationVector minimumTranslationVector = mtv;
        float overlap = Float.MAX_VALUE;
        float max1 = 0.0f;
        float min1 = 0.0f;
        int end12 = offset1 + count1;
        int end2 = offset2 + count2;
        int i2 = offset1;
        while (true) {
            char c = 1;
            if (i2 < end12) {
                float x1 = verts1[i2];
                float y1 = verts1[i2 + 1];
                float x2 = verts1[(i2 + 2) % count1];
                float y2 = verts1[(i2 + 3) % count1];
                float axisX = y1 - y2;
                float axisY = -(x1 - x2);
                float length = (float) Math.sqrt((double) ((axisX * axisX) + (axisY * axisY)));
                float axisX2 = axisX / length;
                float axisY2 = axisY / length;
                float min12 = (verts1[0] * axisX2) + (verts1[1] * axisY2);
                float max12 = min12;
                for (int j = offset1; j < end12; j += 2) {
                    float p2 = (verts1[j] * axisX2) + (verts1[j + 1] * axisY2);
                    if (p2 < min12) {
                        min12 = p2;
                    } else if (p2 > max12) {
                        max12 = p2;
                    }
                }
                float min2 = (verts2[0] * axisX2) + (verts2[1] * axisY2);
                float max2 = min2;
                int j2 = offset2;
                float f = min2;
                int numInNormalDir = 0;
                float min22 = f;
                while (j2 < end2) {
                    float smallestAxisX = max1;
                    float smallestAxisX2 = max12;
                    float smallestAxisY = min1;
                    float min13 = min12;
                    float x12 = x1;
                    float min23 = min22;
                    float axisY3 = axisY2;
                    int end13 = end12;
                    float axisX3 = axisX2;
                    float length2 = length;
                    numInNormalDir -= pointLineSide(x1, y1, x2, y2, verts2[j2], verts2[j2 + 1]);
                    float p3 = (axisX3 * verts2[j2]) + (axisY3 * verts2[j2 + 1]);
                    if (p3 < min23) {
                        min22 = p3;
                    } else if (p3 > max2) {
                        max2 = p3;
                        min22 = min23;
                    } else {
                        min22 = min23;
                    }
                    j2 += 2;
                    axisY2 = axisY3;
                    max12 = smallestAxisX2;
                    min12 = min13;
                    axisX2 = axisX3;
                    length = length2;
                    max1 = smallestAxisX;
                    min1 = smallestAxisY;
                    x1 = x12;
                    end12 = end13;
                    MinimumTranslationVector minimumTranslationVector2 = mtv;
                }
                float smallestAxisX3 = max1;
                float smallestAxisY2 = min1;
                int end14 = end12;
                float smallestAxisX4 = max12;
                float smallestAxisY3 = min12;
                float axisY4 = axisY2;
                float axisX4 = axisX2;
                float f2 = length;
                float f3 = x1;
                float min24 = min22;
                if ((smallestAxisY3 > min24 || smallestAxisX4 < min24) && (min24 > smallestAxisY3 || max2 < smallestAxisY3)) {
                    return false;
                }
                float o = Math.min(smallestAxisX4, max2) - Math.max(smallestAxisY3, min24);
                if ((smallestAxisY3 < min24 && smallestAxisX4 > max2) || (min24 < smallestAxisY3 && max2 > smallestAxisX4)) {
                    float mins = Math.abs(smallestAxisY3 - min24);
                    float maxs = Math.abs(smallestAxisX4 - max2);
                    o = mins < maxs ? o + mins : o + maxs;
                }
                if (o < overlap) {
                    overlap = o;
                    max1 = numInNormalDir >= 0 ? axisX4 : -axisX4;
                    min1 = numInNormalDir >= 0 ? axisY4 : -axisY4;
                } else {
                    max1 = smallestAxisX3;
                    min1 = smallestAxisY2;
                }
                i2 += 2;
                MinimumTranslationVector minimumTranslationVector3 = mtv;
                end12 = end14;
            } else {
                float smallestAxisY4 = min1;
                int end15 = end12;
                int i3 = offset2;
                float overlap2 = overlap;
                float smallestAxisX5 = max1;
                float smallestAxisY5 = smallestAxisY4;
                while (i3 < end2) {
                    float x13 = verts2[i3];
                    float axisY5 = verts2[i3 + 1];
                    float axisX5 = verts2[(i3 + 2) % count2];
                    float y22 = verts2[(i3 + 3) % count2];
                    float axisX6 = axisY5 - y22;
                    float axisY6 = -(x13 - axisX5);
                    float length3 = (float) Math.sqrt((double) ((axisX6 * axisX6) + (axisY6 * axisY6)));
                    float axisX7 = axisX6 / length3;
                    float axisY7 = axisY6 / length3;
                    float max13 = (verts1[0] * axisX7) + (verts1[c] * axisY7);
                    int numInNormalDir2 = 0;
                    float max14 = max13;
                    int j3 = offset1;
                    while (true) {
                        end1 = end15;
                        if (j3 >= end1) {
                            break;
                        }
                        float p4 = (verts1[j3] * axisX7) + (verts1[j3 + 1] * axisY7);
                        end15 = end1;
                        float max15 = max14;
                        float max16 = x13;
                        float x14 = x13;
                        float min14 = max13;
                        int j4 = j3;
                        float y12 = axisY5;
                        float axisY8 = axisY7;
                        float x22 = axisX5;
                        float axisX8 = axisX7;
                        float length4 = length3;
                        numInNormalDir2 -= pointLineSide(max16, axisY5, axisX5, y22, verts1[j3], verts1[j3 + 1]);
                        if (p4 < min14) {
                            min14 = p4;
                        } else if (p4 > max15) {
                            max15 = p4;
                        }
                        max13 = min14;
                        max14 = max15;
                        j3 = j4 + 2;
                        axisY7 = axisY8;
                        axisX7 = axisX8;
                        length3 = length4;
                        x13 = x14;
                        axisY5 = y12;
                        axisX5 = x22;
                    }
                    float f4 = axisY5;
                    float f5 = axisX5;
                    end15 = end1;
                    float max17 = max14;
                    float min15 = max13;
                    int i4 = j3;
                    float axisY9 = axisY7;
                    float axisX9 = axisX7;
                    float f6 = length3;
                    float min25 = (axisX9 * verts2[0]) + (axisY9 * verts2[1]);
                    float max22 = min25;
                    for (int j5 = offset2; j5 < end2; j5 += 2) {
                        float p5 = (verts2[j5] * axisX9) + (verts2[j5 + 1] * axisY9);
                        if (p5 < min25) {
                            min25 = p5;
                        } else if (p5 > max22) {
                            max22 = p5;
                        }
                    }
                    if ((min15 > min25 || max17 < min25) && (min25 > min15 || max22 < min15)) {
                        return false;
                    }
                    float o2 = Math.min(max17, max22) - Math.max(min15, min25);
                    if ((min15 < min25 && max17 > max22) || (min25 < min15 && max22 > max17)) {
                        float mins2 = Math.abs(min15 - min25);
                        float maxs2 = Math.abs(max17 - max22);
                        o2 = mins2 < maxs2 ? o2 + mins2 : o2 + maxs2;
                    }
                    if (o2 < overlap2) {
                        overlap2 = o2;
                        smallestAxisX5 = numInNormalDir2 < 0 ? axisX9 : -axisX9;
                        smallestAxisY5 = numInNormalDir2 < 0 ? axisY9 : -axisY9;
                    }
                    i3 += 2;
                    c = 1;
                }
                MinimumTranslationVector minimumTranslationVector4 = mtv;
                if (minimumTranslationVector4 == null) {
                    return true;
                }
                minimumTranslationVector4.normal.set(smallestAxisX5, smallestAxisY5);
                minimumTranslationVector4.depth = overlap2;
                return true;
            }
        }
    }

    public static void splitTriangle(float[] triangle, Plane plane, SplitTriangle split) {
        boolean r3;
        boolean r32;
        float[] fArr = triangle;
        Plane plane2 = plane;
        SplitTriangle splitTriangle = split;
        int stride = fArr.length / 3;
        boolean r1 = plane2.testPoint(fArr[0], fArr[1], fArr[2]) == Plane.PlaneSide.Back;
        boolean r2 = plane2.testPoint(fArr[stride + 0], fArr[stride + 1], fArr[stride + 2]) == Plane.PlaneSide.Back;
        boolean r33 = plane2.testPoint(fArr[(stride * 2) + 0], fArr[(stride * 2) + 1], fArr[(stride * 2) + 2]) == Plane.PlaneSide.Back;
        split.reset();
        if (r1 == r2 && r2 == r33) {
            splitTriangle.total = 1;
            if (r1) {
                splitTriangle.numBack = 1;
                System.arraycopy(fArr, 0, splitTriangle.back, 0, fArr.length);
                return;
            }
            splitTriangle.numFront = 1;
            System.arraycopy(fArr, 0, splitTriangle.front, 0, fArr.length);
            return;
        }
        splitTriangle.total = 3;
        splitTriangle.numFront = (r1 ? 0 : 1) + (r2 ? 0 : 1) + (r33 ? 0 : 1);
        splitTriangle.numBack = splitTriangle.total - splitTriangle.numFront;
        splitTriangle.setSide(!r1);
        int second = stride;
        if (r1 != r2) {
            r3 = r33;
            splitEdge(triangle, 0, second, stride, plane, splitTriangle.edgeSplit, 0);
            splitTriangle.add(fArr, 0, stride);
            splitTriangle.add(splitTriangle.edgeSplit, 0, stride);
            splitTriangle.setSide(!split.getSide());
            splitTriangle.add(splitTriangle.edgeSplit, 0, stride);
        } else {
            r3 = r33;
            splitTriangle.add(fArr, 0, stride);
        }
        int first = stride;
        int second2 = stride + stride;
        boolean r34 = r3;
        if (r2 != r34) {
            r32 = r34;
            splitEdge(triangle, first, second2, stride, plane, splitTriangle.edgeSplit, 0);
            splitTriangle.add(fArr, first, stride);
            splitTriangle.add(splitTriangle.edgeSplit, 0, stride);
            splitTriangle.setSide(!split.getSide());
            splitTriangle.add(splitTriangle.edgeSplit, 0, stride);
        } else {
            r32 = r34;
            splitTriangle.add(fArr, first, stride);
        }
        int first2 = stride + stride;
        boolean r35 = r32;
        if (r35 != r1) {
            boolean z = r35;
            splitEdge(triangle, first2, 0, stride, plane, splitTriangle.edgeSplit, 0);
            splitTriangle.add(fArr, first2, stride);
            splitTriangle.add(splitTriangle.edgeSplit, 0, stride);
            splitTriangle.setSide(!split.getSide());
            splitTriangle.add(splitTriangle.edgeSplit, 0, stride);
        } else {
            splitTriangle.add(fArr, first2, stride);
        }
        if (splitTriangle.numFront == 2) {
            System.arraycopy(splitTriangle.front, stride * 2, splitTriangle.front, stride * 3, stride * 2);
            System.arraycopy(splitTriangle.front, 0, splitTriangle.front, stride * 5, stride);
            return;
        }
        System.arraycopy(splitTriangle.back, stride * 2, splitTriangle.back, stride * 3, stride * 2);
        System.arraycopy(splitTriangle.back, 0, splitTriangle.back, stride * 5, stride);
    }

    private static void splitEdge(float[] vertices, int s2, int e2, int stride, Plane plane, float[] split, int offset) {
        float t = intersectLinePlane(vertices[s2], vertices[s2 + 1], vertices[s2 + 2], vertices[e2], vertices[e2 + 1], vertices[e2 + 2], plane, intersection);
        split[offset + 0] = intersection.x;
        split[offset + 1] = intersection.y;
        split[offset + 2] = intersection.z;
        for (int i2 = 3; i2 < stride; i2++) {
            float a = vertices[s2 + i2];
            split[offset + i2] = ((vertices[e2 + i2] - a) * t) + a;
        }
    }

    public static class SplitTriangle {
        public float[] back;
        int backOffset = 0;
        float[] edgeSplit;
        public float[] front;
        boolean frontCurrent = false;
        int frontOffset = 0;
        public int numBack;
        public int numFront;
        public int total;

        public SplitTriangle(int numAttributes) {
            this.front = new float[(numAttributes * 3 * 2)];
            this.back = new float[(numAttributes * 3 * 2)];
            this.edgeSplit = new float[numAttributes];
        }

        public String toString() {
            return "SplitTriangle [front=" + Arrays.toString(this.front) + ", back=" + Arrays.toString(this.back) + ", numFront=" + this.numFront + ", numBack=" + this.numBack + ", total=" + this.total + "]";
        }

        /* access modifiers changed from: package-private */
        public void setSide(boolean front2) {
            this.frontCurrent = front2;
        }

        /* access modifiers changed from: package-private */
        public boolean getSide() {
            return this.frontCurrent;
        }

        /* access modifiers changed from: package-private */
        public void add(float[] vertex, int offset, int stride) {
            if (this.frontCurrent) {
                System.arraycopy(vertex, offset, this.front, this.frontOffset, stride);
                this.frontOffset += stride;
                return;
            }
            System.arraycopy(vertex, offset, this.back, this.backOffset, stride);
            this.backOffset += stride;
        }

        /* access modifiers changed from: package-private */
        public void reset() {
            this.frontCurrent = false;
            this.frontOffset = 0;
            this.backOffset = 0;
            this.numFront = 0;
            this.numBack = 0;
            this.total = 0;
        }
    }
}
