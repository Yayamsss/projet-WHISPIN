import javafx.scene.transform.Affine;

public class Matrix3x2
{
    public static final Matrix3x2 sIdentity = new Matrix3x2(
        1.0f,
        0.0f,
        0.0f,
        1.0f,
        0.0f,
        0.0f);
    
    public float xx, xy;
    public float yx, yy;
    public float zx, zy;

    public Matrix3x2()
    {
        this(sIdentity);
    }

    public Matrix3x2(Matrix3x2 mtx)
    {
        xx = mtx.xx;
        xy = mtx.xy;
        yx = mtx.yx;
        yy = mtx.yy;
        zx = mtx.zx;
        zy = mtx.zy;
    }

    public Matrix3x2(
        float _xx, float _xy,
        float _yx, float _yy,
        float _zx, float _zy)
    {
        xx = _xx;
        xy = _xy;
        yx = _yx;
        yy = _yy;
        zx = _zx;
        zy = _zy;
    }

    public void set(Matrix3x2 other)
    {
        this.xx = other.xx;
        this.xy = other.xy;
        this.yx = other.yx;
        this.yy = other.yy;
        this.zx = other.zx;
        this.zy = other.zy;
    }

    /* Determinant des 2 premières lignes et colonnes */
    public float det()
    {
        return xx * yy - xy * yx;
    }

    public void mul(Matrix3x2 other)
    {
        /* Colonne X */
        float _xx = this.xx * other.xx + this.yx * other.xy;
        float _xy = this.xy * other.xx + this.yy * other.xy;
        /* Colonne Y */
        float _yx = this.xx * other.yx + this.yx * other.yy;
        float _yy = this.xy * other.yx + this.yy * other.yy;
        /* Colonne Z */
        float _zx = this.xx * other.zx + this.yx * other.zy + this.zx;
        float _zy = this.xy * other.zx + this.yy * other.zy + this.zy;

        this.xx = _xx;
        this.xy = _xy;
        this.yx = _yx;
        this.yy = _yy;
        this.zx = _zx;
        this.zy = _zy;
    }

    public Vector2 mul(Vector2 other)
    {
        return new Vector2(
            this.xx * other.x + this.yx * other.y + this.zx,
            this.xy * other.x + this.yy * other.y + this.zy);
    }

    public void inverse()
    {
        float invDet = this.det();

        if (Math.abs(invDet) < 0.0000001f)
        {
            throw new IllegalStateException("Impossible d'inverser une matrice avec un determinant null.");
        }

        invDet = 1.0f / invDet;

        float tempXx = xx;
        float tempXy = xy;
        float tempYx = yx;
        float tempYy = yy;
        float tempZx = zx;
        float tempZy = zy;

        xx =  tempYy * invDet;
        xy = -tempXy * invDet;
        yx = -tempYx * invDet;
        yy =  tempXx * invDet;

        zx = -(tempZx * xx + tempZy * xy);
        zy = -(tempZx * yx + tempZy * yy);
    }

    public static Matrix3x2 translation(float x, float y)
    {
        return new Matrix3x2(
            1.0f, 0.0f,
            0.0f, 1.0f,
            x, y);
    }

    public static Matrix3x2 translation(Vector2 v)
    {
        return translation(v.x, v.y);
    }

    public static Matrix3x2 rotation(float degrees)
    {
        Vector2 rightVec = Vector2.rotate(Vector2.sRight, degrees);
        Vector2 upVec = Vector2.rotate(Vector2.sUp, degrees);

        return new Matrix3x2(
            rightVec.x, rightVec.y,
            upVec.x, upVec.y,
            0.0f, 0.0f);
    }

    public static Matrix3x2 scale(float scale)
    {
        return new Matrix3x2(
            scale, 0.0f,
            0.0f, scale,
            0.0f, 0.0f);
    }

    public static Matrix3x2 scale(float scaleX, float scaleY)
    {
        return new Matrix3x2(
            scaleX,
            0.0f,
            0.0f,
            scaleY,
            0.0f,
            0.0f);
    }

    public static Matrix3x2 transform(float positionX, float positionY, float rotation, float scale)
    {
        Vector2 rightVec = Vector2.mul(Vector2.rotate(Vector2.sRight, rotation), scale);
        Vector2 upVec = Vector2.mul(Vector2.rotate(Vector2.sUp, rotation), scale);

        return new Matrix3x2(
            rightVec.x, rightVec.y,
            upVec.x, upVec.y,
            positionX, positionY);
    }

    /* NOTE:
        Multiplier deux matrices 3 x 2 est mathématiquement impossible.
        Dans cette fonction, la multiplication se passe comme si il existait
        une 3 ème ligne qui est égal à l'identité (aka 0 0 1) */
    public static Matrix3x2 mul(Matrix3x2 a, Matrix3x2 b)
    {
        Matrix3x2 newMtx = new Matrix3x2(a);
        newMtx.mul(b);
        return newMtx;
    }

    public Affine toAffine()
    {
        return this.toAffine(new Affine());
    }

    public Affine toAffine(Affine mtx)
    {
        mtx.setMxx(this.xx);
        mtx.setMyx(this.xy);
        mtx.setMzx(0.0f);

        mtx.setMxy(this.yx);
        mtx.setMyy(this.yy);
        mtx.setMzy(0.0f);

        mtx.setMxz(0.0f);
        mtx.setMyz(0.0f);
        mtx.setMzz(1.0f);

        mtx.setTx(this.zx);
        mtx.setTy(this.zy);
        mtx.setTz(0.0f);
        
        return mtx;
    }

    @Override public String toString()
    {
        return String.format("[%f, %f, %f]\n[%f, %f, %f]", xx, yx, zx, xy, yy, zy);
    }
}
