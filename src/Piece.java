import java.util.Collection;

public abstract class Piece {

    protected final int position;
    protected final Alliance pieceAlliance;
    protected final PieceType pieceType;
    protected boolean isFirstMove;
    private final int cachedHashCode;

    Piece(final int pos, final Alliance a,final PieceType pieceType,final boolean isFirstMove){
        position=pos;
        pieceAlliance=a;
        this.pieceType=pieceType;
        this.isFirstMove=isFirstMove;
        this.cachedHashCode=computeHashCode();
    }

    private int computeHashCode() {
        int result=pieceType.hashCode();
        result=31*result+pieceAlliance.hashCode();
        result=31*result+position;
        result=31*result+(isFirstMove ? 1:0);
        return result;
    }

    @Override
    public boolean equals(final Object other){
        if(this == other){
            return true;
        }
        if(!(other instanceof Piece)){
            return false;
        }
        final Piece otherPiece=(Piece)other;
        return position==otherPiece.getPiecePosition() && pieceType==otherPiece.getPieceType() &&
               pieceAlliance==otherPiece.getPieceAlliance() && isFirstMove==otherPiece.isFirstMove();
    }

    @Override
    public int hashCode(){
        return this.cachedHashCode;
    }

    public Alliance getPieceAlliance(){
        return this.pieceAlliance;
    }

    public int getPiecePosition(){return this.position;}

    public PieceType getPieceType() {return pieceType;}

    public boolean isFirstMove(){return this.isFirstMove;}

    public void firstMoveDone(){this.isFirstMove=false;}

    public boolean isWhite(){
        if(this.pieceAlliance==Alliance.WHITE) return true;
        else return false;
    }

    public boolean isBlack(){
        if(this.pieceAlliance==Alliance.WHITE) return false;
        else return true;
    }

    public abstract Collection<Move> legalMove(final Board board);

    public abstract Piece movePiece(Move move);

    public int getPieceValue() {
        return this.pieceType.getPieceValue();
    }

    public enum PieceType{

        PAWN("P",1){
            @Override
            public boolean isKing(){
                return false;
            }

            @Override
            public boolean isRook() {
                return false;
            }
        },
        KNIGHT("N",3) {
            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() {
                return false;
            }
        },
        BISHOP("B",3) {
            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() {
                return false;
            }
        },
        ROOK("R",5) {
            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() {
                return true;
            }
        },
        QUEEN("Q",9) {
            @Override
            public boolean isKing() {
                return false;
            }

            @Override
            public boolean isRook() {
                return false;
            }
        },
        KING("K",100000) {
            @Override
            public boolean isKing() {
                return true;
            }

            @Override
            public boolean isRook() {
                return false;
            }
        };

        private String pieceName;
        private int pieceValue;

        PieceType(String pieceName,int pieceValue){
            this.pieceName=pieceName;
            this.pieceValue=pieceValue;
        };

        @Override
        public String toString(){
            return this.pieceName;
        }

        public int getPieceValue(){
            return this.pieceValue;
        }

        public abstract boolean isKing();

        public abstract boolean isRook();

    }

}
