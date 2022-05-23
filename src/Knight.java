import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Knight extends Piece {

    private static final int[] candidateMove={-17,-15,-10,-6,6,10,15,17};

    Knight(final int pos,final Alliance a) {
        super(pos, a,PieceType.KNIGHT,true);
    }

    public Knight(final Alliance a,final int piecePos,final boolean isFirstMove){
        super(piecePos, a,PieceType.KNIGHT,isFirstMove);
    }

    @Override
    public Collection<Move> legalMove(Board board) {

        int candidateDestination;
        final List<Move> legalMove=new ArrayList<>();

        for(final int currentCandidate : candidateMove){
            candidateDestination=this.position+currentCandidate;

            if(BoardUtilities.tileIsValid(candidateDestination)){

                if(firstColExc(this.position,currentCandidate) || secondColExc(this.position,currentCandidate)
                    || seventhColExc(this.position,currentCandidate) || eightColExc(this.position,currentCandidate)){

                    continue;//secondo me è break sto scemo
                }

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

    private  static boolean firstColExc(final int currentPosition, final int candidateOffset){
        return BoardUtilities.FirstCol[currentPosition] && ((candidateOffset==-17) || (candidateOffset==-10) ||
                (candidateOffset==6) || (candidateOffset==15));
    }

    private static boolean secondColExc(final int currentPosition, final int candidateOffset){
        return BoardUtilities.SecondCol[currentPosition] && ((candidateOffset==-10) || (candidateOffset==6));
    }

    private static boolean seventhColExc(final int currentPosition, final int candidateOffset){
        return BoardUtilities.SeventhCol[currentPosition] && ((candidateOffset==10) || (candidateOffset==-6));
    }

    private  static boolean eightColExc(final int currentPosition, final int candidateOffset){
        return BoardUtilities.EigthCol[currentPosition] && ((candidateOffset==17) || (candidateOffset==10) ||
                (candidateOffset==-6) || (candidateOffset==-15));
    }

    @Override
    public String toString(){
        return pieceType.KNIGHT.toString();
    }

    @Override
    public Knight movePiece(Move move) {
        return new Knight(move.getdestinationCoordinate(), move.getMovedPiece().getPieceAlliance());
    }
}
