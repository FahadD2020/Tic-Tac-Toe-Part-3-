/************************************************************
 Software Engineering
 Fahad Dawood, Ethan Hannen.
*************************************************************/

import javax.swing.SwingWorker;

public class Opponent
{
    static Box b;
    static boolean turn;
    static AI aiType= AI.NONE;
    static SwingWorker<Boolean, Void> worker;

    private static int getRandomNumber(int max)
    {
        return (int) ((Math.random() * (max - 1)));
    }
    
    private static Box getRandBox(Grid grid, Config options)
    {
    	int i = 0, j = 0, attempt = 0;
        
        // Restrict random to 20 tries
        while (attempt++ < 20 && turn)
        {
            i = getRandomNumber(options.m);
            j = getRandomNumber(options.n);
            Box box = Game.grid.getBox(i, j);
            if (box.getText() == "")
	            return box;
        }
        
        // Too many randoms, iterate through
        for (i = 0; i < options.m; i++)
        {
            for (j = 0; j < options.n; j++)
            {
                if (!turn)
                    break;
                Box box = Game.grid.getBox(i, j);
                if (box.getText() == "")
                	return box;
            }
        }
        return null;
    }
    
    private static void blockPlayer(String line)
    {
    	
    }
    
    private static boolean analyze(Grid grid, Config options)
    {
    	Line line = Game.grid.getLines(null);

    	/* 
    	 * Priority is to block winning moves by the opponent
    	 * Get lines from last move by player and evaluate first
    	 */
  
    	String sub = Game.xPattern.substring(0, Game.xPattern.length() - 1);
    	String gap = "X_X";
    	
    	if (line.row.contains(sub))
    	{
    		int i = line.row.indexOf(sub);
    		if (i == 0 && line.row.substring(sub.length(), sub.length() + 1).contains("_"))
    		{   // Mark after the pattern
    			b = grid.getBox(line.box.row, sub.length());
    		}
    		else
    		{   // Mark before the pattern
    			b = grid.getBox(line.box.row, i - 1);
    		}
    	}
    	else if (line.col.contains(sub))
    	{
    		int i = line.col.indexOf(sub);
    		if (i == 0 &&  line.col.substring(sub.length(), sub.length() + 1).contains("_"))
    		{   // Mark after the pattern
    			System.out.println("Here");
    			b = Game.grid.getBox(sub.length(), line.box.col);
    		}
    		else
    		{   // Mark before the pattern
    			b = grid.getBox(i - 1, line.box.col);
    		}
    	}
    	else if (line.diag.contains(sub))
    	{
    		System.out.println("diag contains sub");
    		int i = line.diag.indexOf(sub);
    		if (i == 0 && line.diag.substring(sub.length(), sub.length() + 1).contains("_"))
    		{   // Mark after the pattern
    			b = grid.getBox(sub.length() + 1, line.box.col + 1);
    		}
    		else
    		{   // Mark before the pattern
    			b = grid.getBox(i - 1, line.box.col - 1);
    		}
    	}
    	else if (line.anti.contains(sub))
    	{
    		System.out.println("anti contains sub");
    		int i = line.anti.indexOf(sub);
    		if (i == 0 && line.diag.substring(sub.length(), sub.length() + 1).contains("_"))
    		{   // Mark after the pattern
    			b = grid.getBox(i, sub.length());
    		}
    		else
    		{   // Mark before the pattern
    			b = grid.getBox(i + 1, i - 1);
    		}
    	}
    	else if (line.row.contains(gap))
    	{
    		System.out.println("row contains gap");
    		int i = line.row.indexOf(gap);
    		b = grid.getBox(line.box.row, i + 1);
    	}
    	else if (line.col.contains(gap))
    	{
    		System.out.println("col contains gap");
    		int i = line.col.indexOf(gap);
    		b = grid.getBox(i + 1, line.box.col);
    	}
    	else if (line.diag.contains(gap))
    	{
    		System.out.println("dag contains gap");
    		int i = line.diag.indexOf(gap);
    		b = grid.getBox(line.box.row + 1, i + 1);
    	}
    	else if (line.anti.contains(gap))
    	{
    		System.out.println("anti contains gap");
    		int i = line.anti.indexOf(gap);
    		b = grid.getBox(line.box.row - 1, i + 1);
    		
    	}
    	else
    	{
    		b = getRandBox(grid, options);
    	}
    	
    	if (b.getText() == "O")
    		b = getRandBox(grid, options);
    	return true;
    }

    public static void play(Grid grid, Config options)
    {
        b = null;
        turn = true;
        worker = 
            new SwingWorker<Boolean, Void>()
            {
                @Override
                protected Boolean doInBackground() throws Exception
                {
                    try {
                        // Emulate the AI 'thinking'
                        Thread.sleep(getRandomNumber(3000));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    
                    if (aiType == AI.LEVEL_1) // Do basic AI
                    {
                    	b = getRandBox(grid, options);
                    	return true;
                    }
                    // Do 'advanced' AI
                    return analyze(grid, options);
                }
                
                // Once the worker thread is done, update the game board
                protected void done()
                {
                    if (turn && b != null)
						Game.markBlock(b);
                    turn = false;
                }
            };
        worker.execute(); // Execute doInBackground()
    }
}
