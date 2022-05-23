import com.google.common.collect.Iterables;

import java.util.*;

public class Board {

    private final List<Tile> gameBoard;
    private final Collection<Piece> whitePieces;
    private final Collection<Piece> blackPieces;

    private WhitePlayer whitePlayer;
    private BlackPlayer blackPlayer;
    private Player currentPlayer;

    private Board(Builder builder){
        this.gameBoard=createGameBoard(builder);
        this.whitePieces=calculateActivePieces(this.gameBoard,Alliance.WHITE);
        this.blackPieces=calculateActivePieces(this.gameBoard,Alliance.BLACK);

        final Collection<Move> whiteLegalMoves=calculateLegalMoves(this.whitePieces);
        final Collection<Move> blackLegalMoves=calculateLegalMoves(this.blackPieces);

        this.whitePlayer=new WhitePlayer(this,whiteLegalMoves,blackLegalMoves);
        this.blackPlayer=new BlackPlayer(this,whiteLegalMoves,blackLegalMoves);
        this.currentPlayer=builder.nextMoveMaker.choosePlayer(this.whitePlayer,this.blackPlayer);
    }

    public Player whitePlayer(){ return this.whitePlayer; }

    public Player blackPlayer(){
        return this.blackPlayer;
    }

    public Player currentPlayer(){return this.currentPlayer;}

    public Collection<Piece> getBlackPieces(){
        return this.blackPieces;
    }

    public Collection<Piece> getWhitePieces(){
        return this.whitePieces;
    }

    private Collection<Move> calculateLegalMoves(Collection<Piece> pieces) {
        List<Move> legalMoves=new ArrayList<>();

        for(Piece piece : pieces){
            legalMoves.addAll(piece.legalMove(this));
        }
        return legalMoves;
    }

    private Collection<Piece> calculateActivePieces(List<Tile> gameBoard, Alliance alliance) {
        final List<Piece> activePieces=new ArrayList<>();
        int i=0;

        for(Tile tile : gameBoard){
            if(tile.isOccupied()){
                Piece piece=tile.getPiece();
                if(piece.getPieceAlliance()==alliance){
                    activePieces.add(piece);
                    i++;
                }
            }
        }
        System.out.println(i);
        return activePieces;
    }



    

    private static List<Tile> createGameBoard(Builder builder){
        final Tile[] tiles=new Tile[BoardUtilities.NUM_TILES];

        for(int i=0;i<BoardUtilities.NUM_TILES;i++){
            tiles[i]=Tile.createTile(i,builder.boardConfig.get(i));
        }
        return Arrays.asList(tiles.clone());
    }

    public static Board createStandardBoard(){
        final Builder builder=new Builder();

        //Black Layout
        builder.setPiece(new Rook(0,Alliance.BLACK));
        builder.setPiece(new Knight(1,Alliance.BLACK));
        builder.setPiece(new Bishop(2,Alliance.BLACK));
        builder.setPiece(new Queen(3,Alliance.BLACK));
        builder.setPiece(new King(4,Alliance.BLACK));
        builder.setPiece(new Bishop(5,Alliance.BLACK));
        builder.setPiece(new Knight(6,Alliance.BLACK));
        builder.setPiece(new Rook(7,Alliance.BLACK));
        for(int i=8;i<16;i++){
            builder.setPiece(new Pawn(i,Alliance.BLACK));
        }

        //White Layout
        for(int i=48;i<56;i++){
            builder.setPiece(new Pawn(i,Alliance.WHITE));
        }
        builder.setPiece(new Rook(56,Alliance.WHITE));
        builder.setPiece(new Knight(57,Alliance.WHITE));
        builder.setPiece(new Bishop(58,Alliance.WHITE));
        builder.setPiece(new Queen(59,Alliance.WHITE));
        builder.setPiece(new King(60,Alliance.WHITE));
        builder.setPiece(new Bishop(61,Alliance.WHITE));
        builder.setPiece(new Knight(62,Alliance.WHITE));
        builder.setPiece(new Rook(63,Alliance.WHITE));

        //white to move
        builder.setMoveMaker(Alliance.WHITE);


        return builder.build();
    }

    public Iterable<Move> getAllLegaMoves() {
        return Iterables.unmodifiableIterable(Iterables.concat(this.whitePlayer.getLegalMove(),this.blackPlayer.getLegalMove()));
    }

    public static class Builder{

        Map<Integer,Piece> boardConfig;
        Alliance nextMoveMaker;
        Pawn enPassantPawn;

        public Builder(){
            this.boardConfig=new HashMap<>();
        }

        public Builder setPiece(final Piece piece){
            this.boardConfig.put(piece.getPiecePosition(),piece);
            return this;
        }

        public Builder setMoveMaker(final Alliance alliance){
            this.nextMoveMaker=alliance;
            return this;
        }

        public Board build(){
            return new Board(this);
        }

        public void setEnPassantPawn(Pawn enPassantPawn) {
            this.enPassantPawn= enPassantPawn;
        }
    }

    public Tile getTile(int candidateDestination) {
        return gameBoard.get(candidateDestination);
    }

    @Override
    public String toString(){
        StringBuilder builder= new StringBuilder();
        for(int i=0;i<BoardUtilities.NUM_TILES;i++){
            String tileText=this.gameBoard.get(i).toString();
            builder.append(String.format("%3s",tileText));
            if((i+1)%BoardUtilities.NUM_TILES_PER_ROW==0){
                builder.append("\n");
            }
        }
        return builder.toString();
    }
}
