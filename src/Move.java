public class Move {

    protected final Board board;
    protected final Piece movedPiece;
    protected final int destination;
    protected final boolean isFirstMove;

    public static final Move NULL_MOVE=new NullMove();

    private Move(final Board board, final Piece movedPiece, final int destination) {
        this.board = board;
        this.movedPiece = movedPiece;
        this.destination = destination;
        this.isFirstMove=movedPiece.isFirstMove();
    }

    private Move(final Board b,final int destinationCoordinate){
        this.board=b;
        this.destination=destinationCoordinate;
        this.movedPiece=null;
        this.isFirstMove=false;
    }

    @Override
    public int hashCode(){
        final int prime=31;
        int result=1;
        result= prime * result + this.movedPiece.getPiecePosition();
        result= prime * result + this.movedPiece.hashCode();
        result= prime * result + this.destination;
        return result;
    }

    @Override
    public boolean equals(final Object other){
        if(this == other){
            return true;
        }
        if(! (other instanceof Move)){
            return false;
        }
        final Move otherMove=(Move) other;
        return  getCurrentCoordinate()== otherMove.getCurrentCoordinate() &&
                getCurrentCoordinate()==otherMove.getdestinationCoordinate()  && getMovedPiece().equals(otherMove.getMovedPiece());
    }

    public int getdestinationCoordinate() {
        return destination;
    }

    public Piece getMovedPiece(){ return this.movedPiece; }

    public boolean isAttack(){
        return false;
    }

    public boolean isCastlingMove(){
        return false;
    }

    public Piece getAttackedPiece(){
        return null;
    }

    public Board execute() {
        Board.Builder builder=new Board.Builder ();
        for(Piece piece : this.board.currentPlayer ().getActivePieces ()){
            if(!this.movedPiece.equals (piece)){
                builder.setPiece (piece);
            }
        }
        for(Piece piece : this.board.currentPlayer().getOpponent().getActivePieces ()){
            builder.setPiece (piece);
        }
        builder.setPiece(this.movedPiece.movePiece(this));
        builder.setMoveMaker(this.board.currentPlayer().getOpponent().getAlliance());

        return builder.build();
    }

    public static final class MajorAttackMove extends Move{

        public MajorAttackMove(final Board board, final Piece pieceMoved, final int destination, final Piece attackePiece){
            super(board,pieceMoved,destination);
        }

        @Override
        public boolean equals(final Object other){
            return this == other || other instanceof MajorAttackMove && super.equals(other);
        }

        @Override
        public String toString(){
            return movedPiece.getPieceType().toString() + BoardUtilities.getPositionAtCoordinate(this.destination) + "x" +
                    BoardUtilities.getPositionAtCoordinate(this.destination);
        }

    }

    public static final class MajorMove extends Move {
        MajorMove(Board board, Piece movedPiece, int destination) {

            super(board, movedPiece, destination);
        }

        @Override
        public boolean equals(final Object other){
            return this == other || other instanceof MajorMove && super.equals(other);
        }

        @Override
        public String toString(){
            if(movedPiece.getPieceType()== Piece.PieceType.PAWN){
                return BoardUtilities.getPositionAtCoordinate(this.destination);
            }else {
                return movedPiece.getPieceType().toString() + BoardUtilities.getPositionAtCoordinate(this.destination);
            }
        }
    }

    public static class AttackMove extends Move {

        final Piece attackedPiece;

        AttackMove(Board board, Piece movedPiece, int destination, Piece attackedPiece) {
            super(board, movedPiece, destination);
            this.attackedPiece = attackedPiece;
        }

        @Override
        public boolean isAttack(){
            return true;
        }

        @Override
        public Piece getAttackedPiece(){
            return this.attackedPiece;
        }

        @Override
        public int hashCode(){
            return this.attackedPiece.hashCode()+super.hashCode();
        }

        @Override
        public boolean equals(Object other){
            if(this == other){
                return true;
            }
            if(! (other instanceof Move)){
                return false;
            }
            final AttackMove otherAttackMove=(AttackMove) other;
            return super.equals(otherAttackMove) && getAttackedPiece().equals(otherAttackMove.getAttackedPiece());
        }


    }

    public static final class PawnMove extends Move{
        PawnMove(Board board, Piece movedPiece, int destination) {
            super(board, movedPiece, destination);
        }

        @Override
        public boolean equals(final Object other){
            return this==other || other instanceof PawnMove && super.equals(other);
        }

        @Override
        public String toString(){
            return BoardUtilities.getPositionAtCoordinate(this.destination);
        }
    }

    public static class PawnAttackMove extends AttackMove{
        PawnAttackMove(final Board board,final Piece movedPiece,final int destination,final Piece attackedPiece) {
            super(board, movedPiece, destination,attackedPiece);
        }

        @Override
        public boolean equals(final Object other){
            return this==other || other instanceof PawnAttackMove && super.equals(other);
        }

        @Override
        public String toString(){
            return BoardUtilities.getPositionAtCoordinate(this.movedPiece.getPiecePosition()).substring(0,1)+"x"+
                                                           BoardUtilities.getPositionAtCoordinate(this.destination);
        }
    }

    public static final class PawnEnPassantAttackMove extends PawnAttackMove{
        PawnEnPassantAttackMove(final Board board,final Piece movedPiece,final int destination,final Piece attackedPiece) {
            super(board, movedPiece, destination,attackedPiece);
        }

        @Override
        public boolean equals(final Object other){
            return this==other || other instanceof PawnEnPassantAttackMove && super.equals(other);

        }

        @Override
        public Board execute(){
            final Board.Builder builder= new Board.Builder();

            for(final Piece piece : this.board.currentPlayer().getActivePieces()){
                if(!this.movedPiece.equals(piece)){
                    builder.setPiece(piece);
                }
            }
            for(final Piece piece : this.board.currentPlayer().getOpponent().getActivePieces()){
                if(!piece.equals(this.getAttackedPiece())){
                    builder.setPiece(piece);
                }
            }
            builder.setPiece(this.movedPiece.movePiece(this));
            builder.setMoveMaker(this.board.currentPlayer().getAlliance());
            return builder.build();
        }
    }

    public static final class PawnJump extends Move{
        PawnJump(final Board board,final Piece movedPiece,final int destination) {
            super(board, movedPiece, destination);
        }

        @Override
        public Board execute(){
            final Board.Builder builder = new Board.Builder();

            for(final Piece piece : this.board.currentPlayer().getActivePieces()){
                if(!this.movedPiece.equals(piece)){
                    builder.setPiece(piece);
                }
            }
            for(final Piece piece : this.board.currentPlayer().getOpponent().getActivePieces()){
                builder.setPiece(piece);
            }
            final Pawn movedPawn=(Pawn)this.movedPiece.movePiece(this);
            builder.setPiece(movedPawn);
            builder.setEnPassantPawn(movedPawn);
            builder.setMoveMaker(this.board.currentPlayer().getOpponent().getAlliance());

            return builder.build();
        }

        @Override
        public String toString(){
            return BoardUtilities.getPositionAtCoordinate(destination);
        }
    }

    static abstract class CastleMove extends Move{

        protected final Rook castleRook;
        protected final int castleRookStart;
        protected final int castleRookDestination;

        CastleMove(final Board board,final Piece movedPiece,final int destination,final Rook castleRook,final int castleRookStart,final int castleRookDestination) {
            super(board, movedPiece, destination);
            this.castleRook=castleRook;
            this.castleRookStart=castleRookStart;
            this.castleRookDestination=castleRookDestination;
        }

        public Rook getCastleRook(){
            return this.castleRook;
        }

        public boolean isCastlingMove(){
            return true;
        }

        @Override
        public Board execute(){

            final Board.Builder builder = new Board.Builder();

            for(final Piece piece : this.board.currentPlayer().getActivePieces()){
                if(!this.movedPiece.equals(piece) && !this.castleRook.equals(piece)){
                    builder.setPiece(piece);
                }
            }
            for(final Piece piece : this.board.currentPlayer().getOpponent().getActivePieces()){
                builder.setPiece(piece);
            }
            builder.setPiece(movedPiece.movePiece(this));
            builder.setPiece(new Rook(this.castleRookDestination,this.castleRook.getPieceAlliance()));
            builder.setMoveMaker(this.board.currentPlayer().getOpponent().getAlliance());

            return builder.build();
        }

        @Override
        public int hashCode(){
            final int prime=31;
            int result=super.hashCode();
            result=prime*result+this.castleRook.hashCode();
            result=prime*result+this.castleRookDestination;
            return result;
        }

        @Override
        public boolean equals(final Object other){
            if(this==other){
                return true;
            }
            if(!(other instanceof CastleMove)){
                return false;
            }
            final CastleMove otherCastleMove=(CastleMove)other;
            return super.equals(otherCastleMove) && this.castleRook.equals(otherCastleMove.getCastleRook());
        }

    }

    public static class KingSideCastleMove extends CastleMove{
        KingSideCastleMove(final Board board,final Piece movedPiece,final int destination,final Rook castleRook,final int castleRookStart,final int castleRookDestination) {
            super(board, movedPiece, destination,castleRook,castleRookStart,castleRookDestination);
        }
        @Override
        public String toString(){
            return "O-O";
        }

        @Override
        public boolean equals(final Object other){
            return this==other || other instanceof KingSideCastleMove && super.equals(other);
        }
    }

    public static class QueenSideCastleMove extends CastleMove{
        QueenSideCastleMove(final Board board,final Piece movedPiece,final int destination,final Rook castleRook,final int castleRookStart,final int castleRookDestination) {
            super(board, movedPiece, destination,castleRook,castleRookStart,castleRookDestination);
        }
        @Override
        public String toString(){
            return "O-O-O";
        }

        @Override
        public boolean equals(final Object other){
            return this==other || other instanceof QueenSideCastleMove && super.equals(other);
        }
    }

    public static class NullMove extends Move{
        NullMove() {
            super(null, 65);
        }

        @Override
        public Board execute(){
            throw new RuntimeException(("cannot execute the null move"));
        }

        public int getCurrentCoordinate(){ //qui cera override
            return -1;
        }
    }

    public static class MoveFactory{

        private MoveFactory(){
            throw  new RuntimeException(("Not instantiable"));
        }

        public static Move createMove(final Board b,final int currentC,final int destinationC){

            for(final Move move : b.getAllLegaMoves()){
                if(move.getCurrentCoordinate()== currentC && move.getdestinationCoordinate()== destinationC){
                    return move;
                }
            }
            return NULL_MOVE;
        }

    }

    private int getCurrentCoordinate() {
        return this.getMovedPiece().getPiecePosition();
    }

}
