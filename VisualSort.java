import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.geom.*;

public class VisualSort implements MouseListener, KeyListener {
	JFrame frame;
    int height = 500;
    int width = 800;
    boolean inScreen = false;
    boolean clicked = false;
    boolean pressed = false;
    boolean once = true;
    int key;
    public VisualSort() {
        frame = new JFrame("Sorting Visualizer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(width+16, height+39);
        frame.getContentPane().add(new Drawing());
        frame.addMouseListener(this);
        frame.addKeyListener(this);
        frame.setVisible(true);
    }
    public static void main(String[] args) {
        new VisualSort();
    }
    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {
    	inScreen = true;
    }
    public void mouseExited(MouseEvent e) {
    	inScreen = false;
    }
    public void mouseClicked(MouseEvent e) {
    	clicked = true;
    }
    public void keyTyped(KeyEvent e) {}
    public void keyPressed(KeyEvent e) {
        pressed = true;
        key = e.getKeyCode();
    }
    public void keyReleased(KeyEvent e) {
        pressed = false;
        once = true;
    }
    class Drawing extends JComponent {
        int screen;
        int menu;
        String type;
        boolean colour;
        String shape;
        String[] complexity;
        int[] values;
        int a;
        boolean finished;
        int speed;
        int delay;
        int access;
        int latest;
        int comparisons;
        int row;
    	Point m;
		boolean updated;
		boolean call;
		boolean paused;
		boolean exit;
		Object lock = new Object();
        public Drawing() {
        	screen = 0;
        	menu = 0;
        	type = "";
        	shape = "";
        	values = new int[height];
        	for(int i = 0; i < values.length; i++)
        		values[i] = i+1;
        	a = 0;
        	speed = 5;
        	delay = 1000/speed;
        	finished = false;
        	comparisons = 0;
        	access = -1;
        	latest = -1;
        	complexity = new String[3];
        	call = true;
        }
        public void paint(Graphics g) {
			//System.out.printf("paint %d\n", System.currentTimeMillis() / 10);
        	Graphics2D g2 = (Graphics2D)g;
        	if(screen == 0) menu(g);
        	if(screen > 0) {
        		if(pressed && once) {
        			if(key == 32) paused = !paused;
        			if(key == 27){
						exit = true;
						synchronized(lock){
							lock.notifyAll();
						}
					}
        			once = false;
    				frame.setTitle("Sorting Visualizer - "+type+" ("+shape+")"+(paused ? " [Paused]" : ""));
        		}
	        	draw(g);
	        	g2.setFont(new Font("Sans-Serif", Font.PLAIN, 12));
	    		g.setColor(Color.white);
	        	g2.drawString("Sorting Visualizer - Eric Shim", 10, 20);
	        	g2.drawString(values.length+" Numbers", 10, 35);
	        	delay = 1000/speed;
        	}
        	if(screen == 1) {
            	g2.drawString("Sort: "+type, 10, 60);
            	g2.drawString("Best Time Complexity: "+complexity[0], 10, height-40);
            	g2.drawString("Average Time Complexity: "+complexity[1], 10, height-25);
            	g2.drawString("Worst Time Complexity: "+complexity[2], 10, height-10);
        		a++;
        		if(a > delay) {
        			a = 0;
        			screen++;
        		}
        	}
        	if(screen == 2) {
        		g.setColor(Color.white);
            	g2.drawString("Sort: "+type, 10, 60);
            	g2.drawString("Shuffling...", 10, 75);
            	g2.drawString("Best Time Complexity: "+complexity[0], 10, height-40);
            	g2.drawString("Average Time Complexity: "+complexity[1], 10, height-25);
            	g2.drawString("Worst Time Complexity: "+complexity[2], 10, height-10);
        		if(call) {
        			new Shuffle().start();
        			call = false;
        		}
        	}
        	if(screen == 3) {
        		g.setColor(Color.white);
            	g2.drawString("Sort: "+type, 10, 60);
            	g2.drawString("Comparisons: 0", 10, 75);
            	g2.drawString("Best Time Complexity: "+complexity[0], 10, height-40);
            	g2.drawString("Average Time Complexity: "+complexity[1], 10, height-25);
            	g2.drawString("Worst Time Complexity: "+complexity[2], 10, height-10);
        		a++;
        		if(a > delay) {
        			a = 0;
        			screen++;
        		}
        	}
        	if(screen == 4) {
        		g.setColor(Color.white);
            	g2.drawString("Sort: "+type, 10, 60);
            	g2.drawString("Comparisons: "+comparisons, 10, 75);
            	g2.drawString("Best Time Complexity: "+complexity[0], 10, height-40);
            	g2.drawString("Average Time Complexity: "+complexity[1], 10, height-25);
            	g2.drawString("Worst Time Complexity: "+complexity[2], 10, height-10);
            	if(type.equals("Bubble Sort")) {
    				if(call) {
    					speed = 1;
    					new BubbleSort().start();
						call = false;
        			}
    			} else if(type.equals("Insertion Sort")) {
    				if(call) {
    					speed = 1;
    					new InsertionSort().start();
						call = false;
        			}
    			} else if(type.equals("Selection Sort")) {
    				if(call) {
    					speed = 50;
    					new SelectionSort().start();
						call = false;
        			}
    			} else if(type.equals("Merge Sort")) {
    				if(call) {
    					speed = 5;
    					new MergeSort().start();
						call = false;
        			}
        		} else if(type.equals("Quicksort")) {
        			if(call) {
        				speed = 5;
    					new QuickSort().start();
						call = false;
        			}
        		} else {
        			if(call) {
        				speed = 5;
    					new RadixSort().start();
						call = false;
        			}
        		}
        	}
        	if(screen == 5) {
            	speed = 1;
        		g.setColor(Color.white);
            	g2.drawString("Sort: "+type, 10, 60);
            	g2.drawString("Comparisons: "+comparisons, 10, 75);
            	g2.drawString("Best Time Complexity: "+complexity[0], 10, height-40);
            	g2.drawString("Average Time Complexity: "+complexity[1], 10, height-25);
            	g2.drawString("Worst Time Complexity: "+complexity[2], 10, height-10);
        		a++;
        		if(a > values.length) {
        			reset();
        		}
        	}
        	try {Thread.sleep(speed);} catch(Exception e) {}
            repaint();
        }
        public void menu(Graphics g) {
        	exit = false;
        	g.setColor(Color.black);
        	g.fillRect(0, 0, width, height);
        	Graphics2D g2 = (Graphics2D)g;
        	g2.setFont(new Font("Sans-Serif", Font.PLAIN, 36));
    		g.setColor(Color.white);
        	g2.drawString("Sorting Visualizer - Eric Shim", 170, 80);
        	g2.setFont(new Font("Sans-Serif", Font.PLAIN, 18));
    		if(menu == 0) {
	        	if(inScreen) {
	        		try {
	        			m = frame.getMousePosition();
		        		if(m.x > (width+16)/2-75 && m.x < (width+16)/2+75 && m.y > height/2-115 && m.y < height/2-65) {
		        			g.setColor(Color.white);
		        			g.fillRect(width/2-75, height/2-145, 150, 50);
		        			g.setColor(Color.black);
		        			g.drawString("Bubble Sort", width/2-48, height/2-113);
		        			if(clicked) {
		        				type = "Bubble Sort";
		        				complexity[0] = "O(n)";
		        				complexity[1] = complexity[2] = "O(n^2)";
		        				menu++;
		        				frame.setTitle("Sorting Visualizer - "+type);
		        				clicked = false;
		        			}
		        		} else {
		        			g.setColor(Color.white);
		        			g.drawString("Bubble Sort", width/2-48, height/2-113);
		        		}
		        		if(m.x > (width+16)/2-75 && m.x < (width+16)/2+75 && m.y > height/2-55 && m.y < height/2-5) {
		        			g.setColor(Color.white);
		        			g.fillRect(width/2-75, height/2-85, 150, 50);
		        			g.setColor(Color.black);
		        			g.drawString("Insertion Sort", width/2-54, height/2-53);
		        			if(clicked) {
		        				type = "Insertion Sort";
		        				complexity[0] = "O(n)";
		        				complexity[1] = complexity[2] = "O(n^2)";
		        				menu++;
		        				frame.setTitle("Sorting Visualizer - "+type);
		        				clicked = false;
		        			}
		        		} else {
		        			g.setColor(Color.white);
		        			g.drawString("Insertion Sort", width/2-54, height/2-53);
		        		}
		        		if(m.x > (width+16)/2-75 && m.x < (width+16)/2+75 && m.y > height/2+5 && m.y < height/2+55) {
		        			g.setColor(Color.white);
		        			g.fillRect(width/2-75, height/2-25, 150, 50);
		        			g.setColor(Color.black);
		        			g.drawString("Selection Sort", width/2-58, height/2+7);
		        			if(clicked) {
		        				type = "Selection Sort";
		        				complexity[0] = complexity[1] = complexity[2] = "O(n^2)";
		        				menu++;
		        				frame.setTitle("Sorting Visualizer - "+type);
		        				clicked = false;
		        			}
		        		} else {
		        			g.setColor(Color.white);
		        			g.drawString("Selection Sort", width/2-58, height/2+7);
		        		}
		        		if(m.x > (width+16)/2-75 && m.x < (width+16)/2+75 && m.y > height/2+65 && m.y < height/2+115) {
		        			g.setColor(Color.white);
		        			g.fillRect(width/2-75, height/2+35, 150, 50);
		        			g.setColor(Color.black);
		        			g.drawString("Merge Sort", width/2-45, height/2+67);
		        			if(clicked) {
		        				type = "Merge Sort";
		        				complexity[0] = complexity[1] = complexity[2] = "O(n log n)";
		        				menu++;
		        				frame.setTitle("Sorting Visualizer - "+type);
		        				clicked = false;
		        			}
		        		} else {
		        			g.setColor(Color.white);
		        			g.drawString("Merge Sort", width/2-45, height/2+67);
		        		}
		        		if(m.x > (width+16)/2-75 && m.x < (width+16)/2+75 && m.y > height/2+125 && m.y < height/2+175) {
		        			g.setColor(Color.white);
		        			g.fillRect(width/2-75, height/2+95, 150, 50);
		        			g.setColor(Color.black);
		        			g.drawString("Quicksort", width/2-38, height/2+127);
		        			if(clicked) {
		        				type = "Quicksort";
		        				complexity[0] = complexity[1] = "O(n log n)";
		        				complexity[2] = "O(n^2)";
		        				menu++;
		        				frame.setTitle("Sorting Visualizer - "+type);
		        				clicked = false;
		        			}
		        		} else {
		        			g.setColor(Color.white);
		        			g.drawString("Quicksort", width/2-38, height/2+127);
		        		}
		        		if(m.x > (width+16)/2-75 && m.x < (width+16)/2+75 && m.y > height/2+185 && m.y < height/2+235) {
		        			g.setColor(Color.white);
		        			g.fillRect(width/2-75, height/2+155, 150, 50);
		        			g.setColor(Color.black);
		        			g.drawString("Radix Sort", width/2-41, height/2+187);
		        			if(clicked) {
		        				type = "Radix Sort";
		        				complexity[0] = complexity[1] = complexity[2] = "O(nk)";
		        				menu++;
		        				frame.setTitle("Sorting Visualizer - "+type);
		        				clicked = false;
		        			}
		        		} else {
		        			g.setColor(Color.white);
		        			g.drawString("Radix Sort", width/2-41, height/2+187);
		        		}
	        		} catch(NullPointerException e) {}
	        	} else {
	    			g.setColor(Color.white);
	    			g.drawString("Bubble Sort", width/2-48, height/2-113);
	    			g.drawString("Insertion Sort", width/2-54, height/2-53);
	    			g.drawString("Selection Sort", width/2-58, height/2+7);
	    			g.drawString("Merge Sort", width/2-45, height/2+67);
	    			g.drawString("Quicksort", width/2-38, height/2+127);
	    			g.drawString("Radix Sort", width/2-41, height/2+187);
	        	}
        	} else if(menu == 1) {
        		if(inScreen) {
        			try {
	        			m = frame.getMousePosition();
	        			if(m.x > (width+16)/2-75 && m.x < (width+16)/2+75 && m.y > height/2-115 && m.y < height/2-65) {
		        			g.setColor(Color.white);
		        			g.fillRect(width/2-75, height/2-145, 150, 50);
		        			g.setColor(Color.black);
		        			g.drawString("Bars", width/2-18, height/2-113);
		        			if(clicked) {
		        				shape = "Bars";
		        				menu++;
		        				frame.setTitle("Sorting Visualizer - "+type+" ("+shape+")");
		        				clicked = false;
		        			}
		        		} else {
		        			g.setColor(Color.white);
		        			g.drawString("Bars", width/2-18, height/2-113);
		        		}
		        		if(m.x > (width+16)/2-75 && m.x < (width+16)/2+75 && m.y > height/2-55 && m.y < height/2-5) {
		        			g.setColor(Color.white);
		        			g.fillRect(width/2-75, height/2-85, 150, 50);
		        			g.setColor(Color.black);
		        			g.drawString("Pyramid", width/2-32, height/2-53);
		        			if(clicked) {
		        				shape = "Pyramid";
		        				menu++;
		        				frame.setTitle("Sorting Visualizer - "+type+" ("+shape+")");
		        				clicked = false;
		        			}
		        		} else {
		        			g.setColor(Color.white);
		        			g.drawString("Pyramid", width/2-32, height/2-53);
		        		}
		        		if(m.x > (width+16)/2-75 && m.x < (width+16)/2+75 && m.y > height/2+5 && m.y < height/2+55) {
		        			g.setColor(Color.white);
		        			g.fillRect(width/2-75, height/2-25, 150, 50);
		        			g.setColor(Color.black);
		        			g.drawString("Swirl", width/2-20, height/2+7);
		        			if(clicked) {
		        				shape = "Swirl";
		        				menu++;
		        				frame.setTitle("Sorting Visualizer - "+type+" ("+shape+")");
		        				clicked = false;
		        			}
		        		} else {
		        			g.setColor(Color.white);
		        			g.drawString("Swirl", width/2-20, height/2+7);
		        		}
		        		if(m.x > (width+16)/2-75 && m.x < (width+16)/2+75 && m.y > height/2+65 && m.y < height/2+115) {
		        			g.setColor(Color.white);
		        			g.fillRect(width/2-75, height/2+35, 150, 50);
		        			g.setColor(Color.black);
		        			g.drawString("Colour Circle", width/2-51, height/2+67);
		        			if(clicked) {
		        				shape = "Colour Circle";
		        				colour = true;
		        				screen++;
		        				frame.setTitle("Sorting Visualizer - "+type+" ("+shape+")");
		        				clicked = false;
		        			}
		        		} else {
		        			g.setColor(Color.white);
		        			g.drawString("Colour Circle", width/2-51, height/2+67);
		        		}
		        		if(m.x > (width+16)/2-75 && m.x < (width+16)/2+75 && m.y > height/2+125 && m.y < height/2+175) {
		        			g.setColor(Color.white);
		        			g.fillRect(width/2-75, height/2+95, 150, 50);
		        			g.setColor(Color.black);
		        			g.drawString("Scatter Plot", width/2-46, height/2+127);
		        			if(clicked) {
		        				shape = "Scatter Plot";
		        				menu++;
		        				frame.setTitle("Sorting Visualizer - "+type+" ("+shape+")");
		        				clicked = false;
		        			}
		        		} else {
		        			g.setColor(Color.white);
		        			g.drawString("Scatter Plot", width/2-46, height/2+127);
		        		}
		        		if(m.x > (width+16)/2-75 && m.x < (width+16)/2+75 && m.y > height/2+185 && m.y < height/2+235) {
		        			g.setColor(Color.white);
		        			g.fillRect(width/2-75, height/2+155, 150, 50);
		        			g.setColor(Color.black);
		        			g.drawString("Swirl Dots", width/2-40, height/2+187);
		        			if(clicked) {
		        				shape = "Swirl Dots";
		        				menu++;
		        				frame.setTitle("Sorting Visualizer - "+type+" ("+shape+")");
		        				clicked = false;
		        			}
		        		} else {
		        			g.setColor(Color.white);
		        			g.drawString("Swirl Dots", width/2-40, height/2+187);
		        		}
		        		if(m.x > (width+16)/2+85 && m.x < (width+16)/2+235 && m.y > height/2-115 && m.y < height/2-65) {
		        			g.setColor(Color.white);
		        			g.fillRect(width/2+85, height/2-145, 150, 50);
		        			g.setColor(Color.black);
		        			g.drawString("Disparity Circle", width/2+100, height/2-113);
		        			if(clicked) {
		        				shape = "Disparity Circle";
		        				colour = true;
		        				screen++;
		        				frame.setTitle("Sorting Visualizer - "+type+" ("+shape+")");
		        				clicked = false;
		        			}
		        		} else {
		        			g.setColor(Color.white);
		        			g.drawString("Disparity Circle", width/2+100, height/2-113);
		        		}
		        		if(m.x > (width+16)/2+85 && m.x < (width+16)/2+235 && m.y > height/2-55 && m.y < height/2-5) {
		        			g.setColor(Color.white);
		        			g.fillRect(width/2+85, height/2-85, 150, 50);
		        			g.setColor(Color.black);
		        			g.drawString("Disparity Dots", width/2+104, height/2-53);
		        			if(clicked) {
		        				shape = "Disparity Dots";
		        				colour = true;
		        				screen++;
		        				frame.setTitle("Sorting Visualizer - "+type+" ("+shape+")");
		        				clicked = false;
		        			}
		        		} else {
		        			g.setColor(Color.white);
		        			g.drawString("Disparity Dots", width/2+104, height/2-53);
		        		}
		        		if(m.x > (width+16)/2+85 && m.x < (width+16)/2+235 && m.y > height/2+5 && m.y < height/2+55) {
		        			g.setColor(Color.white);
		        			g.fillRect(width/2+85, height/2-25, 150, 50);
		        			g.setColor(Color.black);
		        			g.drawString("Hoops", width/2+134, height/2+7);
		        			if(clicked) {
		        				shape = "Hoops 1";
		        				colour = true;
		        				screen++;
		        				frame.setTitle("Sorting Visualizer - "+type+" ("+shape+")");
		        				clicked = false;
		        			}
		        		} else {
		        			g.setColor(Color.white);
		        			g.drawString("Hoops", width/2+134, height/2+7);
		        		}
		        		if(m.x > (width+16)/2+85 && m.x < (width+16)/2+235 && m.y > height/2+65 && m.y < height/2+115) {
		        			g.setColor(Color.white);
		        			g.fillRect(width/2+85, height/2+35, 150, 50);
		        			g.setColor(Color.black);
		        			g.drawString("Hoops 2", width/2+128, height/2+67);
		        			if(clicked) {
		        				shape = "Hoops 2";
		        				colour = true;
		        				screen++;
		        				frame.setTitle("Sorting Visualizer - "+type+" ("+shape+")");
		        				clicked = false;
		        			}
		        		} else {
		        			g.setColor(Color.white);
		        			g.drawString("Hoops 2", width/2+128, height/2+67);
		        		}
		        		if(m.x > (width+16)/2+85 && m.x < (width+16)/2+235 && m.y > height/2+125 && m.y < height/2+175) {
		        			g.setColor(Color.white);
		        			g.fillRect(width/2+85, height/2+95, 150, 50);
		        			g.setColor(Color.black);
		        			g.drawString("Tri-Mesh", width/2+125, height/2+127);
		        			if(clicked) {
		        				shape = "Tri-Mesh";
		        				colour = true;
		        				screen++;
		        				frame.setTitle("Sorting Visualizer - "+type+" ("+shape+")");
		        				clicked = false;
		        			}
		        		} else {
		        			g.setColor(Color.white);
		        			g.drawString("Tri-Mesh", width/2+125, height/2+127);
		        		}
		        		if(m.x > (width+16)/2+85 && m.x < (width+16)/2+235 && m.y > height/2+185 && m.y < height/2+235) {
		        			g.setColor(Color.white);
		        			g.fillRect(width/2+85, height/2+155, 150, 50);
		        			g.setColor(Color.black);
		        			g.drawString("Rect-Mesh", width/2+117, height/2+187);
		        			if(clicked) {
		        				shape = "Rect-Mesh";
		        				colour = true;
		        				screen++;
		        				frame.setTitle("Sorting Visualizer - "+type+" ("+shape+")");
		        				clicked = false;
		        			}
		        		} else {
		        			g.setColor(Color.white);
		        			g.drawString("Rect-Mesh", width/2+117, height/2+187);
		        		}
		        		if(m.x > (width+16)/2-235 && m.x < (width+16)/2-85 && m.y > height/2-115 && m.y < height/2-65) {
		        			g.setColor(Color.white);
		        			g.fillRect(width/2-235, height/2-145, 150, 50);
		        			g.setColor(Color.black);
		        			g.drawString("Rainbow", width/2-196, height/2-113);
		        			if(clicked) {
		        				shape = "Rainbow";
		        				colour = true;
		        				screen++;
		        				frame.setTitle("Sorting Visualizer - "+type+" ("+shape+")");
		        				clicked = false;
		        			}
		        		} else {
		        			g.setColor(Color.white);
		        			g.drawString("Rainbow", width/2-196, height/2-113);
		        		}
		        		if(m.x > (width+16)/2-235 && m.x < (width+16)/2-85 && m.y > height/2-55 && m.y < height/2-5) {
		        			g.setColor(Color.white);
		        			g.fillRect(width/2-235, height/2-85, 150, 50);
		        			g.setColor(Color.black);
		        			g.drawString("Triangle", width/2-194, height/2-53);
		        			if(clicked) {
		        				shape = "Triangle";
		        				menu++;
		        				frame.setTitle("Sorting Visualizer - "+type+" ("+shape+")");
		        				clicked = false;
		        			}
		        		} else {
		        			g.setColor(Color.white);
		        			g.drawString("Triangle", width/2-194, height/2-53);
		        		}
		        		if(m.x > (width+16)/2-235 && m.x < (width+16)/2-85 && m.y > height/2+5 && m.y < height/2+55) {
		        			g.setColor(Color.white);
		        			g.fillRect(width/2-235, height/2-25, 150, 50);
		        			g.setColor(Color.black);
		        			g.drawString("10Print", width/2-191, height/2+7);
		        			if(clicked) {
		        				shape = "10Print";
		        				colour = true;
		        				screen++;
		        				frame.setTitle("Sorting Visualizer - "+type+" ("+shape+")");
		        				clicked = false;
		        			}
		        		} else {
		        			g.setColor(Color.white);
		        			g.drawString("10Print", width/2-191, height/2+7);
		        		}
		        		if(m.x > (width+16)/2-235 && m.x < (width+16)/2-85 && m.y > height/2+65 && m.y < height/2+115) {
		        			g.setColor(Color.white);
		        			g.fillRect(width/2-235, height/2+35, 150, 50);
		        			g.setColor(Color.black);
		        			g.drawString("Colour Ring", width/2-208, height/2+67);
		        			if(clicked) {
		        				shape = "Colour Ring";
		        				colour = true;
		        				screen++;
		        				frame.setTitle("Sorting Visualizer - "+type+" ("+shape+")");
		        				clicked = false;
		        			}
		        		} else {
		        			g.setColor(Color.white);
		        			g.drawString("Colour Ring", width/2-208, height/2+67);
		        		}
		        		if(m.x > (width+16)/2-235 && m.x < (width+16)/2-85 && m.y > height/2+125 && m.y < height/2+175) {
		        			g.setColor(Color.white);
		        			g.fillRect(width/2-235, height/2+95, 150, 50);
		        			g.setColor(Color.black);
		        			g.drawString("Disparity Graph", width/2-222, height/2+127);
		        			if(clicked) {
		        				shape = "Radial Disparity Graph";
		        				colour = true;
		        				screen++;
		        				frame.setTitle("Sorting Visualizer - "+type+" ("+shape+")");
		        				clicked = false;
		        			}
		        		} else {
		        			g.setColor(Color.white);
		        			g.drawString("Disparity Graph", width/2-222, height/2+127);
		        		}
		        		if(m.x > (width+16)/2-235 && m.x < (width+16)/2-85 && m.y > height/2+185 && m.y < height/2+235) {
		        			g.setColor(Color.white);
		        			g.fillRect(width/2-235, height/2+155, 150, 50);
		        			g.setColor(Color.black);
		        			g.drawString("Butterfly Dots", width/2-214, height/2+187);
		        			if(clicked) {
		        				shape = "Butterfly Dots";
		        				menu++;
		        				frame.setTitle("Sorting Visualizer - "+type+" ("+shape+")");
		        				clicked = false;
		        			}
		        		} else {
		        			g.setColor(Color.white);
		        			g.drawString("Butterfly Dots", width/2-214, height/2+187);
		        		}
	        		} catch(NullPointerException e) {}
        		} else {
	    			g.setColor(Color.white);
        			g.drawString("Bars", width/2-18, height/2-113);
        			g.drawString("Pyramid", width/2-32, height/2-53);
        			g.drawString("Swirl", width/2-20, height/2+7);
        			g.drawString("Colour Circle", width/2-51, height/2+67);
        			g.drawString("Scatter Plot", width/2-46, height/2+127);
        			g.drawString("Swirl Dots", width/2-40, height/2+187);
        			g.drawString("Disparity Circle", width/2+100, height/2-113);
        			g.drawString("Disparity Dots", width/2+104, height/2-53);
        			g.drawString("Hoops", width/2+134, height/2+7);
        			g.drawString("Hoops 2", width/2+128, height/2+67);
        			g.drawString("Tri-Mesh", width/2+125, height/2+127);
        			g.drawString("Rect-Mesh", width/2+117, height/2+187);
        			g.drawString("Rainbow", width/2-196, height/2-113);
        			g.drawString("Triangle", width/2-194, height/2-53);
        			g.drawString("10Print", width/2-191, height/2+7);
        			g.drawString("Colour Ring", width/2-208, height/2+67);
        			g.drawString("Disparity Graph", width/2-222, height/2+127);
        			g.drawString("Butterfly Dots", width/2-214, height/2+187);
	        	}
        	} else if(menu == 2) {
        		if(inScreen) {
        			try {
	        			m = frame.getMousePosition();
	        			if(m.x > (width+16)/2-75 && m.x < (width+16)/2+75 && m.y > height/2+5 && m.y < height/2+55) {
		        			g.setColor(Color.white);
		        			g.fillRect(width/2-75, height/2-25, 150, 50);
		        			g.setColor(Color.black);
		        			g.drawString("Black & White", width/2-55, height/2+7);
		        			if(clicked) {
		        				colour = false;
		        				screen++;
		        				clicked = false;
		        			}
		        		} else {
		        			g.setColor(Color.white);
		        			g.drawString("Black & White", width/2-55, height/2+7);
		        		}
		        		if(m.x > (width+16)/2-75 && m.x < (width+16)/2+75 && m.y > height/2+65 && m.y < height/2+115) {
		        			g.setColor(Color.white);
		        			g.fillRect(width/2-75, height/2+35, 150, 50);
		        			g.setColor(Color.black);
		        			g.drawString("Colour", width/2-27, height/2+67);
		        			if(clicked) {
		        				colour = true;
		        				screen++;
		        				clicked = false;
		        			}
		        		} else {
		        			g.setColor(Color.white);
		        			g.drawString("Colour", width/2-27, height/2+67);
		        		}
	        		} catch(NullPointerException e) {}
        		} else {
        			g.setColor(Color.white);
	    			g.drawString("Black & White", width/2-55, height/2+7);
	    			g.drawString("Colour", width/2-27, height/2+67);
        		}
        	}
        }
        public void draw(Graphics g) {
        	Graphics2D g2 = (Graphics2D)g;
        	synchronized(lock) {
        		g.setColor(Color.black);
        		g.fillRect(0, 0, width, height);
        		row = 0;
        		for(int i = 0; i < values.length; i++) {
        			if(colour) {
        				g.setColor(Color.getHSBColor((float)(values[i]*0.002), 1f, 1f));
        			} else {
        				g.setColor(Color.white);
            		    if(access >= 0 && i == access) g.setColor(Color.red);
            		    if(latest >= 0 && i == latest) g.setColor(Color.green);
        			}
        			if(finished) {
            			if(i <= a) g.setColor(Color.green);
        		    }
        			if(shape.equals("Bars")) g.drawLine(width-height+i, height, width-height+i, height-values[i]);
        			else if(shape.equals("Pyramid")) g.drawLine(width-height/2-values[i]/2, i, width-height/2+values[i]/2, i);
        			else if(shape.equals("Swirl")) g2.fill(new Arc2D.Double(width-height/2-values[i]/2.0, height/2-values[i]/2.0, values[i], values[i], -i*360.0/height, -360.0/height, Arc2D.PIE));
        			else if(shape.equals("Colour Circle")) g2.fill(new Arc2D.Double(width-height, 0, height, height, 90-i*360.0/height, -360.0/height, Arc2D.PIE));
        			else if(shape.equals("Scatter Plot")) g.fillOval(width-height+i-2, height-values[i]-2, 4, 4);
        			else if(shape.equals("Swirl Dots")) g2.fill(new Ellipse2D.Double(width-height/2+(values[i]/2.0-2)*Math.cos(Math.toRadians(i*360.0/height))-2, height/2+(values[i]/2.0-2)*Math.sin(Math.toRadians(i*360.0/height))-2, 4, 4));
        			else if(shape.equals("Disparity Circle")) g2.fill(new Arc2D.Double(width-height/2-disparityDiameter(values[i], i+1)/2, height/2-disparityDiameter(values[i], i+1)/2, disparityDiameter(values[i], i+1), disparityDiameter(values[i], i+1), 90-i*360.0/height, -360.0/height, Arc2D.PIE));
        			else if(shape.equals("Disparity Dots")) g2.fill(new Ellipse2D.Double(width-height/2+(disparityDiameter(values[i], i+1)/2-2)*Math.cos(Math.toRadians(-90+i*360.0/height))-2, height/2+(disparityDiameter(values[i], i+1)/2-2)*Math.sin(Math.toRadians(-90+i*360.0/height))-2, 4, 4));
        			else if(shape.equals("Hoops 1")) g2.draw(new Ellipse2D.Double(width-height/2-(i+1)/2.0, height/2-(i+1)/2.0, i+1, i+1));
        			else if(shape.equals("Hoops 2")) g2.draw(new Ellipse2D.Double(width-height/2-values[i]/2.0, height/2-(i+1)/2.0, values[i], values[i]));
        			else if(shape.equals("Tri-Mesh")) {
        				if(row%2 == 0) {
        					g.fillPolygon(new int[] {width-height+i*25-row*height, width-height+i*25-row*height+25, width-height+i*25-row*height}, new int[] {row*19, row*19+19, row*19+38}, 3);
        					g.setColor(Color.black);
        					g.drawPolygon(new int[] {width-height+i*25-row*height, width-height+i*25-row*height+25, width-height+i*25-row*height}, new int[] {row*19, row*19+19, row*19+38}, 3);
        				} else {
        					g.fillPolygon(new int[] {width-height+i*25-row*height, width-height+i*25-row*height+25, width-height+i*25-row*height+25}, new int[] {row*19+19, row*19, row*19+38}, 3);
        					g.setColor(Color.black);
        					g.drawPolygon(new int[] {width-height+i*25-row*height, width-height+i*25-row*height+25, width-height+i*25-row*height+25}, new int[] {row*19+19, row*19, row*19+38}, 3);
        				}
        				if((i+1)%20 == 0) row++;
        			} else if(shape.equals("Rect-Mesh")) {
        				g.fillRect(width-height+i*25-row*height, row*20, 25, 20);
        				g.setColor(Color.black);
        				g.drawRect(width-height+i*25-row*height, row*20, 25, 20);
        				if((i+1)%20 == 0) row++;
        			} else if(shape.equals("Rainbow")) g.drawLine(width-height+i, 0, width-height+i, height);
        			else if(shape.equals("Triangle")) g.drawLine(width-height+i, height/2-values[i]/2, width-height+i, height/2+values[i]/2);
        			else if(shape.equals("10Print")) {
        				g2.setStroke(new BasicStroke(2));
        				if(values[i]%2 == 0) g.drawLine(width-height+i*25-row*height, row*20, width-height+i*25-row*height+25, row*20+20);
        				else g2.draw(new Line2D.Double(width-height+i*25-row*height, row*20+20, width-height+i*25-row*height+25, row*20));
        				if((i+1)%20 == 0) row++;
        			} else if(shape.equals("Colour Ring")) {
        				g2.setStroke(new BasicStroke(4));
        				g2.draw(new Arc2D.Double(width-height+2, 2, height-4, height-4, 90-i*360.0/height, -360.0/height, Arc2D.OPEN));
        			} else if(shape.equals("Radial Disparity Graph")) {
        				g2.fill(new Ellipse2D.Double(width-height/2+(disparityDiameter(values[i], i+1)/2-2)*Math.cos(Math.toRadians(-90+i*360.0/height))-2, height/2+(disparityDiameter(values[i], i+1)/2-2)*Math.sin(Math.toRadians(-90+i*360.0/height))-2, 4, 4));
        				if(i < values.length-1) g2.draw(new Line2D.Double(width-height/2+(disparityDiameter(values[i], i+1)/2-2)*Math.cos(Math.toRadians(-90+i*360.0/height)), height/2+(disparityDiameter(values[i], i+1)/2-2)*Math.sin(Math.toRadians(-90+i*360.0/height)), width-height/2+(disparityDiameter(values[i+1], i+2)/2-2)*Math.cos(Math.toRadians(-90+(i+1)*360.0/height)), height/2+(disparityDiameter(values[i+1], i+2)/2-2)*Math.sin(Math.toRadians(-90+(i+1)*360.0/height))));
        				else g2.draw(new Line2D.Double(width-height/2+(disparityDiameter(values[i], i+1)/2-2)*Math.cos(Math.toRadians(-90+i*360.0/height)), height/2+(disparityDiameter(values[i], i+1)/2-2)*Math.sin(Math.toRadians(-90+i*360.0/height)), width-height/2+(disparityDiameter(values[0], 1)/2-2)*Math.cos(Math.toRadians(-90)), height/2+(disparityDiameter(values[0], 1)/2-2)*Math.sin(Math.toRadians(-90))));
        			} else if(shape.equals("Butterfly Dots")) {
        				g2.fill(new Ellipse2D.Double(width-height/2+5*Math.cos(Math.toRadians(-90+i*360.0/height))+(butterflyDiameter(values[i])/2-2)*Math.cos(Math.toRadians(-90+i*360.0/height))-2, height/2+5*Math.sin(Math.toRadians(-90+i*360.0/height))+(butterflyDiameter(values[i])/2-2)*Math.sin(Math.toRadians(-90+i*360.0/height))-2, 4, 4));
        			}
        		}
        		if(!paused) {
        			updated = true;
					lock.notifyAll();
        		}
        	}
        }
        private double disparityDiameter(int value, int index) {
        	int loc = Math.abs(index-value);
        	int d = (loc <= 250 ? loc : height-loc);
        	
        	return height-d*2;
        }
        private double butterflyDiameter(int value) {
        	value = value%125;
        	return value < 63 ? value*height/63.0 : (63-(value%63))*height/63.0;
        }
		class StopException extends RuntimeException{}
        private void swap(int a, int b) throws StopException {
			synchronized(lock) {
				while(!updated || paused) {
					try {
						lock.wait();
						if (exit)
							throw new StopException();
					} catch(InterruptedException e) {}
				}
        		int temp = values[a];
        		values[a] = values[b];
        		values[b] = temp;
				updated = false;
			}
        }
        private void set(int i, int x) throws StopException {
        	synchronized(lock) {
				while(!updated || paused) {
					try {
						lock.wait();
						if (exit)
							throw new StopException();
					} catch(InterruptedException e) {}
				}
				values[i] = x;
				updated = false;
			}
        }
        private int set(int x) throws StopException {
        	synchronized(lock) {
				while(!updated || paused) {
					try {
						lock.wait();
						if (exit)
							throw new StopException();
					} catch(InterruptedException e) {}
				}
				updated = false;
				return x;
			}
        }
        private void reset() {
        	for(int i = 0; i < values.length; i++)
        		values[i] = i+1;
        	screen = 0;
        	menu = 0;
        	type = "";
        	shape = "";
        	a = 0;
        	speed = 5;
        	delay = 1000/speed;
        	finished = false;
        	comparisons = 0;
        	access = -1;
        	complexity = new String[3];
        	call = true;
        	paused = false;
        	frame.setTitle("Sorting Visualizer");
        }
        class Shuffle extends Thread {
        	void shuffle() throws StopException {
        		for(int i = 0; i < values.length; i++) {
        			int rPos = (int)(Math.random()*values.length);
        			swap(i, rPos);
        		}
        	}
        	public void run() {
        		try {
					shuffle();
				} catch(StopException e) {
					reset();
					return;
				}
        		screen++;
        		call = true;
        	}
        }
        class BubbleSort extends Thread {
        	void bSort() throws StopException {
        		for(int i = 0; i < values.length; i++) {
        			latest = values.length-i;
        			for(access = 0; access < values.length-1-i; access++) {
            			comparisons++;
        				if(values[access] > values[access+1])
        					swap(access, access+1);
        			}
        		}
        	}
        	public void run() {
        		try {
					bSort();
				} catch(StopException e) {
					reset();
					return;
				}
        		finished = true;
        		screen++;
    			access = -1;
    			latest = -1;
        	}
        }
        class InsertionSort extends Thread {
        	void iSort() throws StopException {
        		for(latest = 0; latest < values.length; latest++) {
        			int key = values[latest];
        			access = latest-1;
        			while(access >= 0 && values[access] > key) {
            			comparisons++;
            			set(access+1, values[access]);
        				access--;
        			}
        			//set(access+1, key);
        			values[access+1] = key;
        		}
        	}
        	public void run() {
        		try {
        			iSort();
        		} catch(StopException e) {
        			reset();
        			return;
        		}
        		finished = true;
        		screen++;
    			access = -1;
    			latest = -1;
        	}
        }
        class SelectionSort extends Thread {
        	void sSort() throws StopException {
        		for(int i = 0; i < values.length-1; i++) {
        			access = i;
        			latest = i-1;
        			for(int j = i+1; j < values.length; j++) {
            			comparisons++;
        				if(values[j] < values[access])
        					access = set(j);
        			}
        			swap(i, access);
        		}
        	}
        	public void run() {
        		try {
        			sSort();
        		} catch(StopException e) {
        			reset();
        			return;
        		}
        		finished = true;
        		screen++;
    			access = -1;
    			latest = -1;
        	}
        }
        class MergeSort extends Thread {
        	void mSort(int l, int r) throws StopException {
        		if(l >= r)
        			return;
        		int m = (l+r)/2;
        		mSort(l, m);
        		mSort(m+1, r);
        		
        		merge(l, m, r);
        	}
        	void merge(int l, int m, int r) throws StopException {
        		int n1 = m-l+1;
        		int n2 = r-m;
        		
        		int[] left = new int[n1];
        		int[] right = new int[n2];
        		
        		for(int i = 0; i < n1; ++i)
                    left[i] = values[l+i];
                for(int i = 0; i < n2; ++i)
                    right[i] = values[m+1+i];
                
                int i = 0, j = 0;
                access = l;
                latest = r;
                while(i < n1 && j < n2) {
                	comparisons++;
                    if (left[i] <= right[j]) {
                    	set(access, left[i]);
                        i++;
                    } else {
                    	set(access, right[j]);
                        j++;
                    }
                    access++;
                }
                while(i < n1) {
                	values[access] = left[i];
                    i++;
                    access++;
                }
                while(j < n2) {
                	values[access] = right[j];
                    j++;
                    access++;
                }
        	}
        	public void run() {
        		try {
        			mSort(0, values.length-1);
        		} catch(StopException e) {
        			reset();
        			return;
        		}
    			finished = true;
    			screen++;
    			access = -1;
    			latest = -1;
        	}
        }
        class QuickSort extends Thread {
    		void qSort(int l, int r) throws StopException {
    			if(l >= r)
    				return;
    			int p = partition(l, r);
    			qSort(l, p-1);
    			qSort(p+1, r);
    		}
        	int partition(int low, int high) throws StopException {
        		latest = values[high];
        		int i = low-1;
        		for(access = low; access < high; access++) {
        			comparisons++;
            		if(values[access] < latest) {
            		    i++;
            		    swap(i, access);
            		}
        		}
        		swap(i+1, high);
        		return i + 1;
        	}
        	public void run()  {
        		try {
        			qSort(0, values.length-1);
        		} catch(StopException e) {
        			reset();
        			return;
        		}
    			finished = true;
    			screen++;
    			access = -1;
    			latest = -1;
    		}
    	}
        class RadixSort extends Thread {
        	void rSort() throws StopException {
        		int m = getMax();
        		
        		for(int exp = 1; m/exp > 0; exp*= 10)
        			countSort(exp);
        	}
        	void countSort(int exp) throws StopException {
                int[] output = new int[values.length];
                int count[] = new int[10];
                
                for(int i = 0; i < values.length; i++)
                    count[(values[i]/exp)%10]++;
                
                for(int i = 1; i < 10; i++)
                    count[i] += count[i-1];
                
                for(int i = values.length - 1; i >= 0; i--) {
                    output[count[(values[i]/exp)%10]-1] = values[i];
                    count[(values[i]/exp)%10]--;
                }
                
                for(access = 0; access < values.length; access++)
                	set(access, output[access]);
            }
        	int getMax() {
                int mx = values[0];
                for(int i = 1; i < values.length; i++)
                    if(values[i] > mx)
                        mx = values[i];
                return mx;
            }
        	public void run() {
        		try {
        			rSort();
        		} catch(StopException e) {
        			reset();
        			return;
        		}
    			finished = true;
    			screen++;
    			access = -1;
        	}
        }
    }
}
