import java.util.HashMap;
import java.util.Map;

public abstract class Tile {

    protected final int coordinate;

    private static final Map<Integer,EmptyTile> EMPTY_TILE_MAP=createPossibleTiles();

    private static Map<Integer, EmptyTile> createPossibleTiles() {
        final Map<Integer,EmptyTile> emptyTileMap=new HashMap<>();

        for(int i = 0; i< BoardUtilities.NUM_TILES; i++){
            emptyTileMap.put(i,new EmptyTile(i));
        }

        return emptyTileMap;
    }

    public static Tile createTile(final int c,final Piece p){
        if(p==null){
            return EMPTY_TILE_MAP.get(c);
        }else{
            return new OccupiedTile(c,p);
        }
    }

    private Tile(int c){
        coordinate=c;
    }

    public abstract boolean isOccupied();

    public abstract Piece getPiece();

    public int getTileCoordinate(){
        return this.coordinate;
    }


    public static final class EmptyTile extends Tile{

        private EmptyTile(int c){
            super(c);
        }

        @Override
        public boolean isOccupied(){
            return false;
        }

        @Override
        public Piece getPiece(){
            return null;
        }

        @Override
        public String toString(){
            return "-";
        }

    }

    public static final  class OccupiedTile extends Tile{

        public Piece pieceOnTile;

        private OccupiedTile(int c, Piece p){
            super(c);
            pieceOnTile=p;
        }

        @Override
        public boolean isOccupied(){
            return true;
        }

        @Override
        public Piece getPiece(){
            return this.pieceOnTile;
        }

        @Override
        public String toString(){
            if(getPiece().isWhite()){
                return getPiece().toString();
            }else{
                return getPiece().toString().toLowerCase();
            }
        }

    }

}
