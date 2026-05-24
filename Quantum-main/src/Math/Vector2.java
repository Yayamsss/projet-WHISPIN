/*
    Auteur: Ryane Menaï
*/

public class Vector2
{
    public float x;
    public float y;

    public static final Vector2 sRight = new Vector2(1.0f, 0.0f);
    public static final Vector2 sUp = new Vector2(0.0f, 1.0f);

    public Vector2()
    {
        x = 0.0f;
        y = 0.0f;
    }

    public Vector2(float _x)
    {
        x = _x;
        y = 0.0f;
    }

    public Vector2(float _x, float _y)
    {
        x = _x;
        y = _y;
    }

    public Vector2(Vector2 other)
    {
        x = other.x;
        y = other.y;
    }

    @Override public String toString()
    {
        return "<" + x + ", " + y + ">";
    }

    public float getMagnitude()
    {
        return (float) java.lang.Math.sqrt(x * x + y * y);
    }

    public void set(float _x, float _y)
    {
        this.x = _x;
        this.y = _y;
    }

    public void set(Vector2 other)
    {
        this.x = other.x;
        this.y = other.y;
    }

    public void normalize()
    {
        float magnitude = this.getMagnitude();

        x /= magnitude;
        y /= magnitude;
    }

    public static float dot(Vector2 a, Vector2 b)
    {
        return a.x * b.x + a.y * b.y;
    }

    public static Vector2 add(Vector2 a, Vector2 b)
    {
        return new Vector2(a.x + b.x, a.y + b.y);
    }

    public static Vector2 sub(Vector2 a, Vector2 b)
    {
        return new Vector2(a.x - b.x, a.y - b.y);
    }

    public static Vector2 mul(Vector2 a, float b)
    {
        return new Vector2(a.x * b, a.y * b);
    }

    public static Vector2 lerp(Vector2 a, Vector2 b, float t)
    {
        return new Vector2(
            a.x + (b.x - a.x) * t,
            a.y + (b.y - a.y) * t);
    }

    public static Vector2 rotate(Vector2 v, float degrees)
    {
        double radians = degrees * 0.0174532f;
        
        float cos = (float) Math.cos(radians);
        float sin = (float) Math.sin(radians);

        return new Vector2(
            v.x * cos - v.y * sin,
            v.y * cos + v.x * sin);
    }
}
