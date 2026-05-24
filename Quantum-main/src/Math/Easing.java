public class Easing
{
    public static float easeOutExpo(float x)
    {
        return x >= 1.0f ? 1.0f : 1.0f - (float) Math.pow(2.0f, -10.0f * x);
    }

    public static float easeOutQuart(float x)
    {
        return 1.0f - (float) Math.pow(1.0f - x, 4);
    }

    public static float easeInQuart(float x)
    {
        float square = x * x;
        return square * square;
    }

    public static float easeInCubic(float x)
    {
        return x * x * x;
    }
}
