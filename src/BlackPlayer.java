import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BlackPlayer extends Player {

    public BlackPlayer(Board board, Collection<Move> whiteLegalMoves, Collection<Move> blackLegalMoves) {
        super(board, blackLegalMoves, whiteLegalMoves);
    }

    @Override
    public Collection<Piece> getActivePieces() {
        return this.board.getBlackPieces();
    }

    @Override
    public Alliance getAlliance() {
        return Alliance.BLACK;
    }

    @Override
    public Player getOpponent() {
        return this.board.whitePlayer();
    }

    @Override
    protected Collection<Move> calculateKingCastles(Collection<Move> playerLegals, Collection<Move> opponentsLegal) {
        final List<Move> kingCastle = new ArrayList<>();

        if (this.playerKing.isFirstMove() && !this.isInCheck()) {
            //black king side
            if (this.board.getTile(5).isOccupied() && !this.board.getTile(6).isOccupied()) {
                final Tile rookTile = this.board.getTile(7);
                if (rookTile.isOccupied() && rookTile.getPiece().isFirstMove) {
                    if (Player.calculateAttackOnTile(5, opponentsLegal).isEmpty() && Player.calculateAttackOnTile(6, opponentsLegal).isEmpty() && rookTile.getPiece().getPieceType().isRook());
                    kingCastle.add(new Move.KingSideCastleMove(this.board,this.playerKing,6,(Rook)rookTile.getPiece(),rookTile.getTileCoordinate(),5));
                }
            }
            //white queen side
            if (!this.board.getTile(1).isOccupied() && !this.board.getTile(2).isOccupied() && !this.board.getTile(3).isOccupied()) {
                final Tile rookTile = this.board.getTile(0);
                if (rookTile.isOccupied() && rookTile.getPiece().isFirstMove) {
                    if (Player.calculateAttackOnTile(1, opponentsLegal).isEmpty() && Player.calculateAttackOnTile(2, opponentsLegal).isEmpty() &&
                        Player.calculateAttackOnTile(3, opponentsLegal).isEmpty()&& rookTile.getPiece().getPieceType().isRook());
                        kingCastle.add(new Move.QueenSideCastleMove(this.board,this.playerKing,2,(Rook)rookTile.getPiece(),rookTile.getTileCoordinate(),3));
                }
            }
        }
        return kingCastle;
    }
}

