import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class WhitePlayer extends Player {

    public WhitePlayer(Board board, Collection<Move> whiteLegalMoves, Collection<Move> blackLegalMoves) {
        super(board,whiteLegalMoves,blackLegalMoves);
    }

    @Override
    public Collection<Piece> getActivePieces() {
        return this.board.getWhitePieces();
    }

    @Override
    public Alliance getAlliance() {
        return Alliance.WHITE;
    }

    @Override
    public Player getOpponent() {
        return this.board.blackPlayer();
    }

    @Override
    protected Collection<Move> calculateKingCastles(Collection<Move> playerLegals, Collection<Move> opponentsLegal) {

        final List<Move> kingCastle= new ArrayList<>();

        if(this.playerKing.isFirstMove() && !this.isInCheck()){
            //white king side
            if (this.board.getTile(61).isOccupied() && !this.board.getTile(62).isOccupied()){
                final Tile rookTile=this.board.getTile(63);
                if(rookTile.isOccupied() && rookTile.getPiece().isFirstMove){
                    if(Player.calculateAttackOnTile(61,opponentsLegal).isEmpty() && Player.calculateAttackOnTile(62,opponentsLegal).isEmpty() && rookTile.getPiece().getPieceType().isRook());
                    kingCastle.add(new Move.KingSideCastleMove(this.board,this.playerKing,62,(Rook)rookTile.getPiece(),rookTile.getTileCoordinate(),61));
                }
            }
            //white queen side
            if(!this.board.getTile(59).isOccupied() && !this.board.getTile(58).isOccupied() && !this.board.getTile(57).isOccupied()){
                final Tile rookTile=this.board.getTile(56);
                if(rookTile.isOccupied() && rookTile.getPiece().isFirstMove){
                    if(Player.calculateAttackOnTile(59,opponentsLegal).isEmpty() && Player.calculateAttackOnTile(58,opponentsLegal).isEmpty() &&
                       Player.calculateAttackOnTile(57,opponentsLegal).isEmpty()&& rookTile.getPiece().getPieceType().isRook());
                    kingCastle.add(new Move.QueenSideCastleMove(this.board,this.playerKing,58,(Rook)rookTile.getPiece(),rookTile.getTileCoordinate(),59));
                }
            }
        }

        return kingCastle;
    }

}
