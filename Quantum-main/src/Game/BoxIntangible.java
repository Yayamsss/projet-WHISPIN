/*
    Auteur: Ryane Menaï
*/

/* Boîte intangible qui ne possède pas d'attribut de collision. */
public abstract class BoxIntangible extends Box
{
    @Override public boolean canMove()
    {
        return false;
    }

    public void setCell(BoxRecursive parent, int i, int j)
    {
        if (mParent != null)
        {
            mParent.removeIntangibleBox(this);
        }

        parent.addIntangibleBox(this, i, j);
    }

    public boolean isOverlaping(Box other)
    {
        return this.mParent == other.mParent && this.mCellI == other.mCellI && this.mCellJ == other.mCellJ;
    }
}
