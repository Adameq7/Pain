package pain;

import javax.swing.*;
import javax.imageio.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.*;
import java.io.*;
import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;

class drawingPanel extends JPanel 
{
    BufferedImage image;
    int pixels[][][];    
    float zoomLevel = 1.0f;
    Color color;
    
    @Override // so scrollbars will show
    public Dimension getPreferredSize() 
    {
        if(image != null)
            return new Dimension((int)(image.getWidth() * zoomLevel), (int)(image.getHeight() * zoomLevel));
        else
            return new Dimension(800, 800);
    }
    
    
    public void setImage(BufferedImage image)
    {
        this.image = image;
	Dimension dimension = new Dimension((int)(image.getWidth() * zoomLevel), (int)(image.getHeight() * zoomLevel));
	setPreferredSize(dimension);
    }
    
    public void paintComponent(Graphics graphic) 
    {
       super.paintComponent(graphic);
       draw(graphic);
//       saveImage();
    }

    public void draw(Graphics g)
    {
        Graphics2D g2d = (Graphics2D) g;

        if(image != null)
        {
            int newImageWidth = (int) (image.getWidth() * zoomLevel);
            int newImageHeight = (int) (image.getHeight() * zoomLevel);
         
            g2d.drawImage(image, 0, 0, newImageWidth, newImageHeight, this);

            for(int i = 0; i < image.getWidth(); i++)
            {
                for(int j = 0; j < image.getHeight(); j++)
                {                    
                    if(pixels[i][j][0] != -1)
                    {
                        Color color = new Color(pixels[i][j][0], pixels[i][j][1], pixels[i][j][2]);
                        g2d.setPaint(color);
                        g2d.fillRect((int) (i * zoomLevel), (int) (j * zoomLevel), (int)zoomLevel, (int)zoomLevel);
                    }
                }
            }
        }
    }
    
    public void saveImage() 
    {
        float tZoomLevel = zoomLevel;
        zoomLevel = 1.0f;
        BufferedImage new_image = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_BGR);
        Graphics2D g2d = new_image.createGraphics();

        try 
        {
           File output = new File("output.png");
           draw(g2d);
           ImageIO.write(new_image, "png", output);
        }
        catch(IOException e) 
        {
           System.out.println(e);
        }
        
        zoomLevel = tZoomLevel;
    }

    void setPixels(int[][][] pixels)
    {
        this.pixels = pixels;
    }
    
    drawingPanel()
    {
        super();
    }
        
    drawingPanel(int[][][] pixels)
    {
        super();
        this.pixels = pixels;
    }    
}

class Edytor
{
    BufferedImage image;
    boolean b = false;
    JFrame main_frame;
    JPanel main_panel;
    JPanel center_panel;
    JToolBar toolbar;
    JScrollPane scroll_pane;
    drawingPanel drawing_panel;
    JLabel label;
    JList colors;
    int[][][] pixels;
    Color color;
    JFrame new_frame;
    Point last_point;

    JButton open_button = new JButton("Open");
    JButton save_button = new JButton("Save");
    JButton zoom_in_button = new JButton("Zoom in");
    JButton zoom_out_button = new JButton("Zoom out");
    JButton up_button = new JButton("Up");
    JButton down_button = new JButton("Down");
    JButton left_button = new JButton("Left");
    JButton right_button = new JButton("Right");

    void loadImage(File imageFile)
    {
        try 
        {
            image = ImageIO.read(imageFile);
            pixels = new int[image.getWidth()][image.getHeight()][3];
            
            for(int i = 0; i < image.getWidth(); i++)
            {
                for(int j = 0; j < image.getHeight(); j++)
                {
                    pixels[i][j][0] = -1;
                    pixels[i][j][1] = -1;
                    pixels[i][j][2] = -1;
                }
            }
//            main_frame.setSize(image.getWidth(), image.getHeight());
        }
        catch (IOException e) 
        {
            System.out.println(e);
	}
    }

    Edytor()
    {
        open_button.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e) 
            {
                JFileChooser chooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter(
                    "PNG Images", "png");
                chooser.setFileFilter(filter);
                int returnVal = chooser.showOpenDialog(main_frame);
                if(returnVal == JFileChooser.APPROVE_OPTION) 
                {
                    loadImage(chooser.getSelectedFile());
                    drawing_panel.setImage(image);
                    drawing_panel.setPixels(pixels);
                    drawing_panel.repaint();
                }
            }
        });
        save_button.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e) 
            {
                drawing_panel.saveImage();
            }
        });
        zoom_in_button.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e) 
            {
                drawing_panel.zoomLevel *= 2;
                if(drawing_panel.zoomLevel > 8)
                    drawing_panel.zoomLevel = 8;
                
                drawing_panel.setImage(image);
                drawing_panel.repaint();
                scroll_pane.getViewport().revalidate();
            }
        });
        zoom_out_button.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e) 
            {
                drawing_panel.zoomLevel /= 2;
                if(drawing_panel.zoomLevel < 1)
                    drawing_panel.zoomLevel = 1;

                drawing_panel.setImage(image);                
                drawing_panel.repaint();
                scroll_pane.getViewport().revalidate();
            }
        });

        up_button.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e) 
            {
                scroll_pane.getVerticalScrollBar().setValue(0);
            }
        });
        down_button.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e) 
            {
                scroll_pane.getVerticalScrollBar().setValue(scroll_pane.getVerticalScrollBar().getMaximum());
            }
        });
        left_button.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e) 
            {
                scroll_pane.getHorizontalScrollBar().setValue(0);
            }
        });
        right_button.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e) 
            {
                scroll_pane.getHorizontalScrollBar().setValue(scroll_pane.getHorizontalScrollBar().getMaximum());
            }
        });

        
        main_frame = new JFrame();
        main_panel = new JPanel(new BorderLayout());
        label = new JLabel("pos: 0, 0");
        color = new Color(0, 0, 0);
        String[] Colors = {"Red", "Green", "Blue", "Choose colour"};
        
        colors = new JList(Colors);
        colors.addListSelectionListener(new ListSelectionListener() 
        {
            public void valueChanged(ListSelectionEvent e) 
            {
                switch(colors.getSelectedIndex())
                {
                    case 0:
                        color = new Color(255, 0, 0);
                    break;
                    case 1:
                        color = new Color(0, 255, 0);
                    break;
                    case 2:
                        color = new Color(0, 0, 255);
                    break;
                    case 3:
                        if(new_frame == null)
                        {
                            new_frame = new JFrame();
                            
                            new_frame.addWindowListener(new WindowListener()
                            {
                                @Override
                                public void windowOpened(WindowEvent e) {
                                }

                                @Override
                                public void windowClosing(WindowEvent e)
                                {
                                    new_frame.setVisible(false);
                                }

                                @Override
                                public void windowClosed(WindowEvent e) {
                                }

                                @Override
                                public void windowIconified(WindowEvent e) {
                                }

                                @Override
                                public void windowDeiconified(WindowEvent e) {
                                }

                                @Override
                                public void windowActivated(WindowEvent e) {
                                }

                                @Override
                                public void windowDeactivated(WindowEvent e) {
                                }
                            });

                            JColorChooser tcc = new JColorChooser();
                            new_frame.setSize(new Dimension(500, 300));

                            new_frame.add(tcc);

                            tcc.getSelectionModel().addChangeListener(new ChangeListener()
                            {
                                @Override
                                public void stateChanged(ChangeEvent e) 
                                {
                                    color = tcc.getColor();
                                }
                            });

                            new_frame.setVisible(true);
                        }
                        else
                        {
                            new_frame.setVisible(true);                            
                        }
                    break;

                }
            }
        });

        main_frame.setTitle("Pain");
        main_frame.setSize(1200, 800);
        main_frame.setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);

        drawing_panel = new drawingPanel(pixels);

        toolbar = new JToolBar();
        toolbar.setRollover(true);
        toolbar.add(save_button);
        toolbar.add(open_button);
        toolbar.add(zoom_in_button);
        toolbar.add(zoom_out_button);
        toolbar.add(up_button);
        toolbar.add(down_button);
        toolbar.add(left_button);
        toolbar.add(right_button);
        

        scroll_pane = new JScrollPane(drawing_panel);
        
        MouseListener MouseL = new MouseListener()
        {
            @Override
            public void mouseClicked(MouseEvent e) 
            {
//                System.out.println("clicked");                
            }

            @Override
            public void mousePressed(MouseEvent e) 
            {
                if(e.getX() > 0 && e.getY() > 0 && e.getX() < image.getWidth() && e.getY() < image.getHeight())
                {
                    pixels[e.getX()][e.getY()][0] = color.getRed();
                    pixels[e.getX()][e.getY()][1] = color.getGreen();
                    pixels[e.getX()][e.getY()][2] = color.getBlue();
                    
                    drawing_panel.repaint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) 
            {
                last_point = null;
            }

            @Override
            public void mouseEntered(MouseEvent e) 
            {
//                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void mouseExited(MouseEvent e) 
            {
//                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        };
        
        MouseMotionListener MouseML = new MouseMotionListener()
        {
            @Override
            public void mouseDragged(MouseEvent e) 
            {                
                if(last_point == null)
                    last_point = e.getPoint();
                else
                {
                    double x = (double)last_point.getX() / drawing_panel.zoomLevel;
                    double y = (double)last_point.getY() / drawing_panel.zoomLevel;

                    double angle = Math.atan2(e.getX() / drawing_panel.zoomLevel - x, e.getY() / drawing_panel.zoomLevel - y) * 180 / Math.PI;
                    double dist = Math.sqrt((e.getX() / drawing_panel.zoomLevel - x) * (e.getX() / drawing_panel.zoomLevel - x) + (e.getY() / drawing_panel.zoomLevel - y) * (e.getY() / drawing_panel.zoomLevel - y));
                    double dist2 = 0;
                    double step = 0.5;
                    
                    while(dist2 < dist)
                    {
                        x += Math.sin(angle * Math.PI / 180) * step;
                        y += Math.cos(angle * Math.PI / 180) * step;
                        dist2 += step;

                        if(x > 0 && y > 0 && x < image.getWidth() && y < image.getHeight())
                        {
                            pixels[(int)x][(int)y][0] = color.getRed();
                            pixels[(int)x][(int)y][1] = color.getGreen();
                            pixels[(int)x][(int)y][2] = color.getBlue();
                        }                    
                    }
                }
                
                drawing_panel.repaint();                

                last_point = e.getPoint();                
//                System.out.println(e.getPoint());
            }

            @Override
            public void mouseMoved(MouseEvent e) 
            {
                label.setText("pos: " + e.getX() + " " + e.getY());                
            }
            
        };        
        
        drawing_panel.addMouseListener(MouseL);
        drawing_panel.addMouseMotionListener(MouseML);
        
//        scroll_pane.add(drawing_panel);
        main_panel.add(toolbar, BorderLayout.PAGE_START);
        
        center_panel = new JPanel(new BorderLayout());
        
        center_panel.add(colors, BorderLayout.LINE_START);
        center_panel.add(scroll_pane, BorderLayout.CENTER);
        
        main_panel.add(center_panel, BorderLayout.CENTER);
        main_panel.add(label, BorderLayout.PAGE_END);
        
        main_frame.add(main_panel);
        
//        main_frame.add(toolbar);
//        main_frame.add(drawing_panel);

        main_frame.setVisible(true);    
    }
}


public class Pain 
{
    
    public static void main(String[] args) 
    {
        Edytor e = new Edytor();        
    }
}
