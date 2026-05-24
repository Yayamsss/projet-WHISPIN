/* 
    Auteur: Yanis Achab
*/

import java.util.*;

public class UndoManager extends GameObject
{
    public enum MessageType
    {
        SaveMove,
        Undo,
        Refresh
    }

    private static class BoxSnapshot
    {
        Box box;
        BoxRecursive parent;
        int cellI;
        int cellJ;

        BoxSnapshot(Box box)
        {
            this.box = box;
            this.parent = box.getParent();
            this.cellI = box.getCellI();
            this.cellJ = box.getCellJ();
        }
    }

    private static class UndoMove
    {
        ArrayList<BoxSnapshot> snapshots = new ArrayList<>();

        void addSnapshot(Box box)
        {
            snapshots.add(new BoxSnapshot(box));
        }
    }

    private static UndoManager sInstance = null;

    private ArrayDeque<UndoMove> mHistory;
    private UndoMove mPendingMove;

    public static UndoManager getInstance()
    {
        return sInstance;
    }

    @Override public void onCreate()
    {
        if (sInstance != null)
        {
            this.destroy();
            return;
        }

        sInstance = this;
        mHistory = new ArrayDeque<>();
        mPendingMove = null;
    }

    @Override public void onDestroy()
    {
        if (sInstance == this)
        {
            sInstance = null;
        }
    }

    public void cancelPendingMove()
    {
        mPendingMove = null;
    }

    public void commitPendingMove()
    {
        if( mPendingMove != null && !mPendingMove.snapshots.isEmpty())
        {
            mHistory.push(mPendingMove);
        }

        mPendingMove = null;
    }

    @Override protected void receiveMsg(GameObject from, int msg)
    {
        if (msg == MessageType.SaveMove.ordinal())
        {
            if(!(from instanceof Box))
            {
                return;
            }

            if (mPendingMove == null)
            {
                mPendingMove = new UndoMove();
            }

            mPendingMove.addSnapshot((Box) from);
        }
        else if (msg == MessageType.Undo.ordinal())
        {
            commitPendingMove();

            if (mHistory.isEmpty())
            {
                return;
            }

            UndoMove lastMove = mHistory.pop();

            for (BoxSnapshot snap : lastMove.snapshots)
            {
                BoxRecursive currentParent = snap.box.getParent();
                int currentI = snap.box.getCellI();
                int currentJ = snap.box.getCellJ();

                if (currentParent != null)
                {
                    currentParent.setBoxNoTransformUpdate(null,currentI,currentJ);
                }

                snap.parent.setBox(snap.box,snap.cellI,snap.cellJ);
                
                if (snap.box.isBoxOfType(Box.BoxType.Player) || snap.box.isBoxOfType(Box.BoxType.Recursive))
                {
                    this.sendMsg(snap.box, MessageType.Refresh.ordinal());
                }
            }
        }
    }
}