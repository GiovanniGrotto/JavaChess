import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class Player {

    protected Board board;
    protected King playerKing;
    protected Collection<Move> legalMove;
    private boolean isInCheck;

    Player(Board board,Collection<Move> legalMove,Collection<Move> opponentMove){
        this.board=board;
        this.playerKing=establishKing();
        //SOLITO FATTO LUI USA CONCAT DI GUAVA
        legalMove.addAll(calculateKingCastles(legalMove,opponentMove));
        this.legalMove= legalMove;
        this.isInCheck=!Player.calculateAttackOnTile(this.playerKing.getPiecePosition(),opponentMove).isEmpty();
    }

    public static Collection<Move> calculateAttackOnTile(int piecePosition, Collection<Move> moves) {
        /*System.out.print("Cerco mosse in:");
        System.out.println(piecePosition);*/
        List<Move> attackMoves=new ArrayList<>();
        for(Move move:moves){
            if(piecePosition==move.getdestinationCoordinate()){
               /* System.out.print("Mossa pericolosa:");
                System.out.print(move.movedPiece);
                System.out.print(" ");
                System.out.println(move.movedPiece.getPiecePosition());*/
                attackMoves.add(move);
            }
        }
        return attackMoves;
    }

    public King getPlayerKing(){return this.playerKing;}

    public Collection<Move> getLegalMove(){ return this.legalMove; }

    private King establishKing() {

        for(Piece piece:getActivePieces()){
            if(piece.getPieceType().isKing()){
                return (King) piece;
            }
        }
        throw new RuntimeException("There is no king in the board");
    }

    public boolean isMoveLegal(Move move){
        return this.legalMove.contains(move);
    }

    public boolean isInCheck(){
        return this.isInCheck;
    }

    public boolean isInCheckMate(){
        return this.isInCheck && !escapeMoves();
    }

    protected boolean escapeMoves() {
        for(Move move: this.legalMove){
            MoveTransition moveTransition=makeMove(move);
            if(moveTransition.getMoveStatus().isDone()){
                return true;
            }
        }
        return false;
    }

    public boolean isInStaleMate(){
        return !isInCheck && !escapeMoves();
    }

    public boolean isCastled(){
        return false;
    }

    public MoveTransition makeMove(Move move){

        if(!isMoveLegal(move)){
            return new MoveTransition(this.board,move,MoveStatus.ILLEGAL_MOVE);
        }

        final Board transitionBoard=move.execute();

        final Collection<Move> kingsAttacks = Player.calculateAttackOnTile (transitionBoard.currentPlayer ().getOpponent().getPlayerKing ().getPiecePosition (),
                                                                            transitionBoard.currentPlayer ().getLegalMove ());

        if(!kingsAttacks.isEmpty()){
            return new MoveTransition(this.board,move,MoveStatus.LEAVES_KING_IN_CHECK);
        }

        return new MoveTransition (transitionBoard,move,MoveStatus.DONE);
    }

    public abstract Collection<Piece> getActivePieces();
    public abstract Alliance getAlliance();
    public abstract Player getOpponent();

    protected abstract Collection<Move> calculateKingCastles(Collection<Move> playerLegals,Collection<Move> opponentsLegal);


}
