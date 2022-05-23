import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static javax.swing.SwingUtilities.isLeftMouseButton;
import static javax.swing.SwingUtilities.isRightMouseButton;

public class Table {

    private static final Dimension OUTER_FRAME_DIMENSION = new Dimension(850,750);
    private final static Dimension BOARD_PANEL_DIMENSION= new Dimension(500,450);
    private final static Dimension TILE_PANEL_DIMENSION= new Dimension(10,10);
    private static String pieceIconPath="C:\\Users\\GiovanniG\\Desktop\\Chess\\Image\\"; //80px per i pezzi


    private final Color lightTileColor=Color.decode("#F6F2F1");
    private final Color darkTileColor=Color.decode("#4C7BE0");

    private final JFrame gameFrame;
    private final GameHistoryPanel gameHistoryPanel;
    private final TakenPiecesPanel takenPiecesPanel;
    private final BoardPanel boardPanel;
    private Board chessBoard;
    private final MoveLog moveLog;

    private Tile sourceTile;
    private Tile destinationTile;
    private Piece humanMovedPiece;

    public Table(){
        this.gameFrame= new JFrame("CHESS");
        this.gameFrame.setLayout(new BorderLayout());
        final JMenuBar tableMenuBar=createTableMenuBar();
        this.gameFrame.setJMenuBar(tableMenuBar);
        this.gameFrame.setSize(OUTER_FRAME_DIMENSION);
        this.chessBoard=Board.createStandardBoard();
        this.gameHistoryPanel=new GameHistoryPanel();
        this.takenPiecesPanel= new TakenPiecesPanel();
        this.boardPanel=new BoardPanel();
        this.moveLog=new MoveLog();
        this.gameFrame.add(this.takenPiecesPanel,BorderLayout.WEST);
        this.gameFrame.add(this.boardPanel,BorderLayout.CENTER);
        this.gameFrame.add(this.gameHistoryPanel,BorderLayout.EAST);
        this.gameFrame.setVisible(true);
    }

    private JMenuBar createTableMenuBar() {
        final JMenuBar tableMenuBar=new JMenuBar();
        tableMenuBar.add(createFileMenu());
        tableMenuBar.add(createExitMenu());
        return tableMenuBar;
    }

    private JMenu createFileMenu() {
        final JMenu fileMenu= new JMenu("File");
        final JMenuItem openPGN=new JMenu("Load PGN");
        openPGN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               System.out.println("Open PGN");
            }
        });
        fileMenu.add(openPGN);

        /*final JMenuItem exitMenuItem = new JMenuItem(("Exit"));
        exitMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        fileMenu.add(exitMenuItem);*/

        return fileMenu;
    }


    private JMenuItem createExitMenu(){
        final JMenuItem exitMenu=new JMenuItem("Exit");
        exitMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        return exitMenu;
    }

    public enum BoardDirection{

        NORMAL{
            @Override
            List<TilePanel> traverse(final List<TilePanel> boardTiles){
                return boardTiles;
            }

            @Override
            BoardDirection opposite() {
                return FLIPPED;
            }
        },
        FLIPPED{
            @Override
            List<TilePanel> traverse(final List<TilePanel> boardTiles){
               // return Collections.reverse(boardTiles);
                return null;
            }

            @Override
            BoardDirection opposite(){
                return NORMAL;
            }
        };

        abstract List<TilePanel> traverse(final List<TilePanel> boardTiles);
        abstract BoardDirection opposite();
    }

    private class BoardPanel extends JPanel{
        final List<TilePanel> boardTile;

        BoardPanel(){
            super(new GridLayout(8,8));
            this.boardTile=new ArrayList<>();
            for(int i=0;i<BoardUtilities.NUM_TILES;i++){
                final TilePanel tilePanel=new TilePanel(this,i);
                this.boardTile.add(tilePanel);
                add(tilePanel);
            }
            setPreferredSize(BOARD_PANEL_DIMENSION);
            validate();
        }

        public void drawBoard(Board board) {
            removeAll();
            for(final TilePanel tilePanel : boardTile){
                tilePanel.drawTile(board);
                add(tilePanel);
            }
            validate();
            repaint();
        }
    }

    public static class MoveLog{

        private final List<Move> moves;

        MoveLog(){
            this.moves= new ArrayList<>();
        }

        public List<Move> getMoves(){
            return this.moves;
        }

        public void addMove(final Move move){
            this.moves.add(move);
        }

        public int size(){
            return this.moves.size();
        }

        public void clear(){
            this.moves.clear();
        }

        public Move removeMove(int index){
            return this.moves.remove(index);
        }

        public Boolean removeMove(final Move move){
            return this.moves.remove(move);
        }
    }

    private class TilePanel extends JPanel{

        private final int tileId;

        TilePanel(final BoardPanel boardPanel,final int tileId){
            super(new GridBagLayout());
            this.tileId=tileId;
            setPreferredSize(TILE_PANEL_DIMENSION);
            assignTileColor();
            assignTilePieceIcon(chessBoard);

            addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(final MouseEvent e) {

                    if(isRightMouseButton(e)){ //annulla ogni selezione precedentemente fatta
                        sourceTile=null;
                        destinationTile=null;
                        humanMovedPiece=null;
                    }else if(isLeftMouseButton(e)){
                        if(sourceTile==null){
                            sourceTile=chessBoard.getTile(tileId);
                            if(sourceTile.isOccupied() && sourceTile.getPiece().getPieceAlliance()==chessBoard.currentPlayer().getAlliance()) {
                                humanMovedPiece = sourceTile.getPiece();
                                System.out.println(sourceTile.getPiece());
                                if (humanMovedPiece == null) {
                                    sourceTile = null;
                                }
                            }else{ sourceTile=null;}
                        }else{
                            destinationTile=chessBoard.getTile(tileId);
                            if((!destinationTile.isOccupied())||(destinationTile.getPiece().getPieceAlliance()!=sourceTile.getPiece().getPieceAlliance())) {
                                final Move move = Move.MoveFactory.createMove(chessBoard, sourceTile.getTileCoordinate(), destinationTile.getTileCoordinate());
                                System.out.println(move.destination);
                                final MoveTransition transition = chessBoard.currentPlayer().makeMove(move);
                                System.out.println(chessBoard);
                                System.out.println(transition.getMoveStatus());
                                if (transition.getMoveStatus().isDone()) {
                                    chessBoard = transition.getBoard();
                                    moveLog.addMove(move);
                                }
                                sourceTile = null;
                                destinationTile = null;
                                humanMovedPiece = null;
                            }else{
                                sourceTile=destinationTile;
                                humanMovedPiece = sourceTile.getPiece();
                                System.out.println(sourceTile.getPiece());
                                if (humanMovedPiece == null) {
                                    sourceTile = null;
                                }
                            }
                        }
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                gameHistoryPanel.redo(chessBoard,moveLog);
                                takenPiecesPanel.redo(moveLog);
                                boardPanel.drawBoard(chessBoard);
                            }
                        });
                    }
                }


                @Override
                public void mousePressed(final MouseEvent e) {

                }

                @Override
                public void mouseReleased(final MouseEvent e) {

                }

                @Override
                public void mouseEntered(final MouseEvent e) {

                }

                @Override
                public void mouseExited(final MouseEvent e) {

                }
            });

            validate();
        }

        private void highlightLegals(final Board board){
            for(final Move move : pieceLegalMoves(board)){
                if((move.getdestinationCoordinate()==this.tileId)){
                    try{
                        add(new JLabel(new ImageIcon(ImageIO.read(new File("C:\\Users\\GiovanniG\\Desktop\\Chess\\Image\\greyDot1.png")))));
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }


        private Collection<Move> pieceLegalMoves(final Board board){
            if(humanMovedPiece != null && humanMovedPiece.getPieceAlliance() == board.currentPlayer().getAlliance()){
                return humanMovedPiece.legalMove(board);
            }
            return Collections.emptyList();
        }

        public void drawTile(final Board board){
            assignTileColor();
            assignTilePieceIcon(board);
            highlightLegals(chessBoard);
            validate();
            repaint();
        }

        private void assignTilePieceIcon(final Board board){
            this.removeAll();
            if(board.getTile(this.tileId).isOccupied()){
                try {
                    File pathName=new File(pieceIconPath + board.getTile(this.tileId).getPiece().getPieceAlliance().toString().substring(0,1) +
                            board.getTile(this.tileId).getPiece().toString() + ".png");
                    System.out.println(pathName);
                    final BufferedImage image = ImageIO.read(new File(pieceIconPath + board.getTile(this.tileId).getPiece().getPieceAlliance().toString().substring(0,1) +
                    board.getTile(this.tileId).getPiece().toString() + ".png")); //forse png
                    this.add(new JLabel(new ImageIcon(image)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void assignTileColor() {
            if(BoardUtilities.EIGHTH_RANK[this.tileId] || BoardUtilities.SIXTH_RANK[this.tileId] ||
               BoardUtilities.FOURTH_RANK[this.tileId] || BoardUtilities.SECOND_RANK[this.tileId]){
                setBackground(this.tileId%2==0 ? lightTileColor : darkTileColor);
            }else if(BoardUtilities.SEVENTH_RANK[this.tileId] || BoardUtilities.FIFTH_RANK[this.tileId] ||
                    BoardUtilities.THIRD_RANK[this.tileId] || BoardUtilities.FIRST_RANK[this.tileId]){
                setBackground(this.tileId%2 !=0 ? lightTileColor : darkTileColor);
            }
        }
    }
}
