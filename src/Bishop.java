import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Bishop extends Piece {

    private final static int[] candidateMoveVector = {-9, -7, 7, 9};

    Bishop(int pos, Alliance a) {
        super(pos, a,PieceType.BISHOP,true);
    }

    public Bishop(final Alliance a,final int piecePos,final boolean isFirstMove){
        super(piecePos, a,PieceType.BISHOP,isFirstMove);
    }

    @Override
    public Collection<Move> legalMove(Board board) {

        final List<Move> legalMove = new ArrayList<>();

        for (final int candidateOffest : candidateMoveVector) {

            int candidateDestination = this.position;

            while (BoardUtilities.tileIsValid(candidateDestination)) {

                if(firstColExc(candidateDestination,candidateOffest) || eightColExc(candidateDestination,candidateOffest)){  //secpndo me è or no and
                    break;
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
                        break;
                    }
                }
            }
        }
        return legalMove;
    }

    @Override
    public Bishop movePiece(Move move) {
        return new Bishop(move.getdestinationCoordinate(), move.getMovedPiece().getPieceAlliance());
    }

    private static boolean firstColExc(final int currentPosition,final int candidateOffset){
        return BoardUtilities.FirstCol[currentPosition] && (candidateOffset==-9 || candidateOffset==7);
    }

    private static boolean eightColExc(final int currentPosition,final int candidateOffset){
        return BoardUtilities.EigthCol[currentPosition] && (candidateOffset==-7 || candidateOffset==9);
    }

    @Override
    public String toString(){
        return pieceType.BISHOP.toString();
    }

}
