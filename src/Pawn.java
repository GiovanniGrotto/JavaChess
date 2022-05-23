import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Pawn extends Piece {

    private static int[] candidateMoveVector={7,8,9,16};

    Pawn(int pos, Alliance a) {
        super(pos, a,PieceType.PAWN,true);
    }

    public Pawn(final Alliance a,final int piecePos,final boolean isFirstMove){
        super(piecePos, a,PieceType.PAWN,isFirstMove);
    }

    @Override
    public Collection<Move> legalMove(Board board) {

        final List<Move> legalMove=new ArrayList<>();

        for(final int candidateOffset : candidateMoveVector){

            int candidateDestination=this.position+(this.pieceAlliance.getDirection()*candidateOffset);

                if(!BoardUtilities.tileIsValid(candidateDestination)){
                    continue;
                }

                if((candidateOffset ==8) && !board.getTile(candidateDestination).isOccupied()){
                    legalMove.add(new Move.MajorMove(board,this,candidateDestination));

                }else if(candidateOffset==16 && this.isFirstMove() &&
                        ((BoardUtilities.SEVENTH_RANK[this.position] && this.isBlack()) ||
                                (BoardUtilities.SECOND_RANK[this.position] && this.isWhite()))){
                    final int behindDestination=this.position+(this.pieceAlliance.getDirection()*8);
                    if(!board.getTile(behindDestination).isOccupied() && !board.getTile(candidateDestination).isOccupied()){
                        legalMove.add(new Move.PawnJump(board,this,candidateDestination));
                    }

                }else if(candidateOffset==7 && (BoardUtilities.EigthCol[this.position] && this.isWhite()) ||
                        (BoardUtilities.FirstCol[this.position] && !this.isWhite())){
                        if(board.getTile(candidateDestination).isOccupied()){
                            Piece occupantPiece=board.getTile(candidateDestination).getPiece();
                            if(occupantPiece.pieceAlliance!=this.pieceAlliance){
                                legalMove.add(new Move.PawnAttackMove(board,this,candidateDestination,occupantPiece));
                            }
                        }

                }else if(candidateOffset==9 && (BoardUtilities.FirstCol[this.position] && this.isWhite()) ||
                        (BoardUtilities.EigthCol[this.position] && !this.isWhite())){
                      if(board.getTile(candidateDestination).isOccupied()){
                          Piece occupantPiece=board.getTile(candidateDestination).getPiece();
                          if(occupantPiece.pieceAlliance!=this.pieceAlliance){
                              legalMove.add(new Move.PawnAttackMove(board,this,candidateDestination,occupantPiece));
                          }
                      }

                }
            }
        return legalMove;
    }

    @Override
    public String toString(){
        return pieceType.PAWN.toString();
    }

    @Override
    public Pawn movePiece(Move move) {
        return new Pawn(move.getdestinationCoordinate(), move.getMovedPiece().getPieceAlliance());
    }
}
