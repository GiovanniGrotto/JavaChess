import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class King extends Piece{

    private final static int[] candidateMoveVector = {-9,-8,-7,-1, 1, 7, 8, 9};


    King(int pos, Alliance a) {
        super(pos, a, PieceType.KING,true);
    }

    public King(final Alliance a,final int piecePos,final boolean isFirstMove){
        super(piecePos, a,PieceType.KING,isFirstMove);
    }

    @Override
    public Collection<Move> legalMove(Board board) {

        final List<Move> legalMove=new ArrayList<>();

        for (final int candidateOffest : candidateMoveVector) {

            int candidateDestination = this.position;

                if(firstColExc(this.position,candidateOffest) || eightColExc(this.position,candidateOffest)){
                    continue;
                }

                candidateDestination += candidateOffest;

                if (BoardUtilities.tileIsValid(candidateDestination)) {

                    final Tile candidateDestinationTile=board.getTile(candidateDestination);
                    if(!candidateDestinationTile.isOccupied()){
                        legalMove.add(new Move.MajorMove(board,this,candidateDestination));
                    }else{
                        final Piece pieceAtdestination=candidateDestinationTile.getPiece();
                        final Alliance pieceAtAlliance=pieceAtdestination.getPieceAlliance();
                        if(this.pieceAlliance!=pieceAtAlliance){
                            legalMove.add(new Move.AttackMove(board,this,candidateDestination,pieceAtdestination));
                        }
                    }
                }
        }
        return legalMove;
    }

    @Override
    public King movePiece(Move move) {
        return new King(move.getdestinationCoordinate(), move.getMovedPiece().getPieceAlliance());
    }

    private static boolean firstColExc(final int currentPosition,final int candidateOffset){
        return BoardUtilities.FirstCol[currentPosition] && (candidateOffset==-1 || candidateOffset==-9 || candidateOffset==7);
    }

    private static boolean eightColExc(final int currentPosition,final int candidateOffset){
        return BoardUtilities.EigthCol[currentPosition] && (candidateOffset==1 || candidateOffset==9 || candidateOffset==-7);
    }

    @Override
    public String toString(){
        return pieceType.KING.toString();
    }
}
