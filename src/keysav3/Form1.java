/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package keysav3;
import java.io.*;
import java.nio.file.*;
import java.nio.charset.Charset;
import java.util.*;
import javax.swing.JOptionPane;
import javax.swing.JFileChooser;
import javax.swing.filechooser.*;
import java.awt.datatransfer.StringSelection;
import java.awt.Toolkit;

/**
 *
 * @author Thomas
 */
public class Form1 extends javax.swing.JFrame {

    /**
     * Creates new form Form1
     */
    public Form1()
    {
        initComponents();
        CB_Game.setSelectedIndex(0);
        toggleFilter(null);
        
        // Initialize some UI stuff
        CB_Game.setSelectedIndex(0);
        CB_MainLanguage.setSelectedIndex(0);
        CB_BoxStart.setSelectedIndex(0);
        changeboxsetting(null);
        CB_BoxEnd.setSelectedIndex(0);
        CB_BoxEnd.setEnabled(false);
        CB_Team.setSelectedIndex(0);
        CB_ExportStyle.setSelectedIndex(0);
        CB_BoxColor.setSelectedIndex(0);
        CB_No_IVs.setSelectedIndex(0);
        updatePreview();
        
        // Load configuration, initialize strings
        // loadINI();
        // Set onFormClose to run when program is closed
        InitializeStrings();
        
        // Create some data arrays for our getLevel function
        // This data doesn't change, ever
        String[] temp = getStringList("expTable", "all");
        expTable = new int[101][6];
        int i = 0;
        for (String line : temp)
        {
            String[] temp2 = line.split(",");
            for (int j = 0; j < temp2.length; j++)
            {
                expTable[i][j] = Integer.parseInt(temp2[j]);
            }
            i++;
        }
    }
    
    // Global Variables
    
    // Finding the 3DS SD Files
    private static final String path_exe = System.getProperty("user.dir");
    private static final String datapath = path_exe + System.getProperty("file.separator") + "data";
    private static final String dbpath = path_exe + System.getProperty("file.separator") + "db";
    private static final String bakpath = path_exe + System.getProperty("file.separator") + "backup";
    private String path_3DS = "";
    private String lastOpenedFilename = "";

    // Static data
    private static String[] expGrowth;
    private static int[][] expTable;

    // Language
    private String[] natures;
    private String[] types;
    private String[] abilitylist;
    private String[] movelist;
    private String[] itemlist;
    private String[] specieslist;
    private String[] balls;
    private String[] formlist;
    private String[] countryList;
    private String[] regionList;
    private String[] gameList;
    private String[] vivlist;
    private static final String[] unownlist = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "!", "?" };
    // Blank File Egg Names
    private static final String[] eggnames = { "タマゴ", "Egg", "Œuf", "Uovo", "Ei", "", "Huevo", "알" };
    private static final String[] languageList = { "???", "JPN", "ENG", "FRE", "ITA", "GER", "???", "ESP", "KOR" };

    // Inputs
    private byte[] savefile = new byte[0x10009C];
    private byte[] savkey = new byte[0xB4AD4];
    private byte[] batvideo = new byte[0x100000]; // whatever
    private byte[] zerobox = new byte[232 * 30];

    // Dumping Usage
    private String binType = "sav";
    private String vidpath = "";
    private String savpath = "D:\\Documents\\Pokemon\\Generation 6\\Y Saves";
    private String savkeypath = "";
    private String vidkeypath = "";
    private String custom1 = "";
    private String custom2 = "";
    private String custom3 = "";
    private String customcsv = "";
    private boolean custom1b = false;
    private boolean custom2b = false;
    private boolean custom3b = false;
    private static final String[] boxcolors = new String[] { "", "###", "####", "#####", "######" };
    private String csvdata = "";
    private int dumpedcounter = 0;
    private int slots = 0;
    private boolean ghost = false;
    private ArrayList selectedTSVs = new ArrayList(0);
    private String defaultCSVcustom = "{59},{42},{0},{1},{2},{3},{4},{5},{68},{6},{7},{8},{9},{10},{11},{54},{47},{48},{49},{50},{51},{52},{53},{12},{60},{61},{35},{34},{13},{14},{15},{16},{18},{19},{17},{20},{21},{22},{23},{24},{25},{55},{56},{57},{63},{26},{27},{28},{29},{30},{31},{32},{33},{58},{36},{44},{37},{38},{39},{40},{41},{43},{45},{46},{62},{64},{66},{65}";

    // Breaking Usage
    private byte[] break1 = new byte[0x10009C];
    private byte[] break2 = new byte[0x10009C];
    private byte[] break3 = new byte[0x10009C];
    private byte[] video1 = new byte[28256];
    private byte[] video2 = new byte[28256];

    // UI Usage
    private boolean updateIVCheckboxes = true;
    private volatile int game;
    
    // Drag & Drop Events
    // @TODO
    
    // Utility
    public void onFormClose()
    {
        // Save the ini file
        saveINI();
    }
    
    private void loadINI()
    {
        try
        {
            // Detect startup path and data path.
            if (!Files.isDirectory(Paths.get(datapath))) // Create data path if it doesn't exist.
                Files.createDirectory(Paths.get(datapath));
            if (!Files.isDirectory(Paths.get(dbpath))) // Create db path if it doesn't exist.
                Files.createDirectory(Paths.get(dbpath));
            if (!Files.isDirectory(Paths.get(bakpath))) // Create backup path if it doesn't exist.
                Files.createDirectory(Paths.get(bakpath));

            // Load .ini data.
            if (!Files.exists(Paths.get(datapath, "config.ini")))
                Files.createFile(Paths.get(datapath, "config.ini"));
            else
            {
                ListIterator<String> config = Files.readAllLines(Paths.get(datapath, "config.ini"), Charset.forName("UTF-16LE")).listIterator();

                // Load the data
                tab_Main.setSelectedIndex(Integer.parseInt(config.next()));
                custom1 = config.next();
                custom2 = config.next();
                custom3 = config.next();
                customcsv = config.next();
                custom1b = (Integer.parseInt(config.next()) != 0);
                custom2b = (Integer.parseInt(config.next()) != 0);
                custom3b = (Integer.parseInt(config.next()) != 0);
                CB_ExportStyle.setSelectedIndex(Integer.parseInt(config.next()));
                CB_MainLanguage.setSelectedIndex(Integer.parseInt(config.next()));
                CB_Game.setSelectedIndex(Integer.parseInt(config.next()));
                CHK_MarkFirst.setSelected(Integer.parseInt(config.next()) != 0);
                CHK_Split.setSelected(Integer.parseInt(config.next()) != 0);
                CHK_BoldIVs.setSelected(Integer.parseInt(config.next()) != 0);
                CHK_ShowESV.setSelected(Integer.parseInt(config.next()) != 0);
                CHK_NameQuotes.setSelected(Integer.parseInt(config.next()) != 0);
                CB_BoxColor.setSelectedIndex(Integer.parseInt(config.next()));
                CHK_ColorBox.setSelected(Integer.parseInt(config.next()) != 0);
                CHK_HideFirst.setSelected(Integer.parseInt(config.next()) != 0);
                this.setSize(Integer.parseInt(config.next()), Integer.parseInt(config.next()));
                CHK_Unicode.setSelected(Integer.parseInt(config.next()) != 0);
            }
        }
        catch (IOException e) { JOptionPane.showMessageDialog(this, "Ini config file loading failed.\n\n" + e, "Error", JOptionPane.ERROR_MESSAGE); }
    }
    
    private void saveINI()
    {
        try
        {
            // Detect startup path and data path
            if (!Files.isDirectory(Paths.get(datapath))) // Create data path if it doesn't exist.
                Files.createDirectory(Paths.get(datapath));
            
            // Load .ini data
            if (!Files.exists(Paths.get(datapath, "config.ini")))
                Files.createFile(Paths.get(datapath, "config.ini"));
            else
            {
                // Save the data
                List<String> config = new ArrayList();
                config.add(Integer.toString(tab_Main.getSelectedIndex()));
                config.add(custom1);
                config.add(custom2);
                config.add(custom3);
                config.add(customcsv);
                config.add(Boolean.toString(custom1b));
                config.add(Boolean.toString(custom2b));
                config.add(Boolean.toString(custom3b));
                config.add(Integer.toString(CB_ExportStyle.getSelectedIndex()));
                config.add(Integer.toString(CB_MainLanguage.getSelectedIndex()));
                config.add(Integer.toString(CB_Game.getSelectedIndex()));
                config.add(Boolean.toString(CHK_MarkFirst.isSelected()));
                config.add(Boolean.toString(CHK_Split.isSelected()));
                config.add(Boolean.toString(CHK_BoldIVs.isSelected()));
                config.add(Boolean.toString(CHK_ShowESV.isSelected()));
                config.add(Boolean.toString(CHK_NameQuotes.isSelected()));
                config.add(Integer.toString(CB_BoxColor.getSelectedIndex()));
                config.add(Boolean.toString(CHK_ColorBox.isSelected()));
                config.add(Boolean.toString(CHK_HideFirst.isSelected()));
                config.add(Integer.toString(this.getHeight()));
                config.add(Integer.toString(this.getWidth()));
                config.add(Boolean.toString(CHK_Unicode.isSelected()));
                Files.write(Paths.get(datapath, "config.ini"), config, Charset.forName("UTF-16LE"));
            }
        }
        catch (IOException e) { JOptionPane.showMessageDialog(this, "Ini config file saving failed.\n\n" + e, "Error", JOptionPane.ERROR_MESSAGE); }
    }
    
    // RNG
    private static long LCRNG(long seed)
    {
        return (seed * 0x41C64E6D + 0x00006073) & 0xFFFFFFFFL;
    }
    private static final Random rand = new Random();
    private static int rnd32()
    {
        return (rand.nextInt(1 << 30)) << 2 | (rand.nextInt(1 << 2));
    }

    // PKX Struct Manipulation
    private byte[] shuffleArray(byte[] pkx, long sv)
    {
        byte[] ekx = new byte[260]; System.arraycopy(pkx, 0, ekx, 0, 8);

        // Now to shuffle the blocks

        // Define Shuffle Order Structure
        byte[] aloc = new byte[] { 0, 0, 0, 0, 0, 0, 1, 1, 2, 3, 2, 3, 1, 1, 2, 3, 2, 3, 1, 1, 2, 3, 2, 3 };
        byte[] bloc = new byte[] { 1, 1, 2, 3, 2, 3, 0, 0, 0, 0, 0, 0, 2, 3, 1, 1, 3, 2, 2, 3, 1, 1, 3, 2 };
        byte[] cloc = new byte[] { 2, 3, 1, 1, 3, 2, 2, 3, 1, 1, 3, 2, 0, 0, 0, 0, 0, 0, 3, 2, 3, 2, 1, 1 };
        byte[] dloc = new byte[] { 3, 2, 3, 2, 1, 1, 3, 2, 3, 2, 1, 1, 3, 2, 3, 2, 1, 1, 0, 0, 0, 0, 0, 0 };

        // Get Shuffle Order
        byte[] shlog = new byte[] { aloc[(int)sv], bloc[(int)sv], cloc[(int)sv], dloc[(int)sv] };

        // UnShuffle Away!
        for (int b = 0; b < 4; b++)
            //ekx = Arrays.copyOfRange(pkx, b, b);
            System.arraycopy(pkx, 8 + 56 * shlog[b], ekx, 8 + 56 * b, 56);

        // Fill the Battle Stats back
        if (pkx.length > 232)
            System.arraycopy(pkx, 232, ekx, 232, 28);
        return ekx;
    }

    private byte[] decryptArray(byte[] ekx)
    {
        byte[] pkx = new byte[0xE8]; System.arraycopy(ekx, 0, pkx, 0, 0xE8);
        long pv = BitConverter.ToUInt32(pkx, 0);
        long sv = (((pv & 0x3E000) >> 0xD) % 24);

        long seed = pv;

        // Decrypt Blocks with RNG Seed
        for (int i = 8; i < 232; i += 2)
        {
            int pre = pkx[i] + ((pkx[i + 1]) << 8);
            seed = LCRNG(seed);
            int seedxor = (int)(seed) >> 16;
            int post = (pre ^ seedxor);
            pkx[i] = (byte)((post) & 0xFF);
            pkx[i + 1] = (byte)(((post) >> 8) & 0xFF);
        }

        // Deshuffle
        pkx = shuffleArray(pkx, sv);
        return pkx;
    }

    private byte[] encryptArray(byte[] pkx)
    {
        // Shuffle
        long pv = BitConverter.ToUInt32(pkx, 0);
        long sv = (((pv & 0x3E000) >> 0xD) % 24);

        byte[] ekxdata = new byte[pkx.length]; System.arraycopy(pkx, 0, ekxdata, 0, pkx.length);

        // If I unshuffle 11 times, the 12th (decryption) will always decrypt to ABCD.
        // 2 x 3 x 4 = 12 (possible unshuffle loops -> total iterations)
        for (int i = 0; i < 11; i++)
            ekxdata = shuffleArray(ekxdata, sv);

        long seed = pv;
        // Encrypt Blocks with RNG Seed
        for (int i = 8; i < 232; i += 2)
        {
            int pre = ekxdata[i] + ((ekxdata[i + 1]) << 8);
            seed = LCRNG(seed);
            int seedxor = (int)((seed) >> 16);
            int post = (pre ^ seedxor);
            ekxdata[i] = (byte)((post) & 0xFF);
            ekxdata[i + 1] = (byte)(((post) >> 8) & 0xFF);
        }

        // Encrypt the Party Stats
        seed = pv;
        for (int i = 232; i < 260; i += 2)
        {
            int pre = ekxdata[i] + ((ekxdata[i + 1]) << 8);
            seed = LCRNG(seed);
            int seedxor = (int)((seed) >> 16);
            int post = (pre ^ seedxor);
            ekxdata[i] = (byte)((post) & 0xFF);
            ekxdata[i + 1] = (byte)(((post) >> 8) & 0xFF);
        }

        // Done
        return ekxdata;
    }

    private int getDloc(int ec)
    {
        // Define Shuffle Order Structure
        byte[] dloc = new byte[] { 3, 2, 3, 2, 1, 1, 3, 2, 3, 2, 1, 1, 3, 2, 3, 2, 1, 1, 0, 0, 0, 0, 0, 0 };
        int sv = (((ec & 0x3E000) >> 0xD) % 24);

        return dloc[sv];
    }

    private boolean verifyCHK(byte[] pkx)
    {
        int chk = 0;
        for (int i = 8; i < 232; i += 2) // Loop through the entire PKX
            chk += BitConverter.ToUInt16(pkx, i);

        int actualsum = BitConverter.ToUInt16(pkx, 0x6);
        if ((BitConverter.ToUInt16(pkx, 0x8) > 750) || (BitConverter.ToUInt16(pkx, 0x90) != 0)) 
            return false;
        return (chk == actualsum);
    }
    
    // File Type Loading
    private void B_OpenSAV_Click(java.awt.event.ActionEvent evt)
    {
        JFileChooser ofd = new JFileChooser(savpath);
        ofd.addChoosableFileFilter(new FileNameExtensionFilter("Save", "sav", "bin"));
        if (ofd.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
        {
            File file = ofd.getSelectedFile();
            lastOpenedFilename = file.getName();
            savpath = ofd.getCurrentDirectory().toString();
            openSAV(file);
        }
    }

    private void B_OpenVid_Click(java.awt.event.ActionEvent evt)
    {
        JFileChooser ofd = new JFileChooser(vidpath);
        ofd.addChoosableFileFilter(new FileNameExtensionFilter("Battle Video", "*"));
        if (ofd.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
        {
            File file = ofd.getSelectedFile();
            lastOpenedFilename = file.getName();
            vidpath = ofd.getCurrentDirectory().toString();
            openVID(file);
        }
    }
    
    private void openSAV(File path)
    {
        long len = path.length();
        if (len == 232*30*31)
        {
            // PCEdit pcdata.bin
            binType = "raw";
            openBIN(path);
        }
        else if (len == 232*30*32)
        {
            // Cu3PO42's boxes.bin
            binType = "yabd";
            openBIN(path);
        }
        else if (len == 0x70000)
        {
            // RAMSAV for XY
            binType = "xy";
            openBIN(path);
        }
        else if (len == 0x80000)
        {
            // RAMSAV for ORAS
            binType = "oras";
            openBIN(path);
        }
        else
        {
            binType = "sav";
            openSAV_(path, true);
        }
    }

    private int openSAV_(File path, boolean showUI)
    {
        try
        {
            // check to see if good input file
            long len = path.length();
            if (len != 0x100000 && len != 0x10009C && len != 0x10019A)
            { 
                if (showUI)
                    JOptionPane.showMessageDialog(this, "Incorrect File Loaded: Not a SAV file (1MB).", "Error", JOptionPane.ERROR_MESSAGE);
                return 0;
            }

            TB_SAV.setText(path.getPath());

            // Go ahead and load the save file into RAM...
            byte[] input = Files.readAllBytes(path.toPath());
            System.arraycopy(input, input.length % 0x100000, savefile, 0, 0x100000);

            // Fetch Stamp
            long stamp = BitConverter.ToUInt64(savefile, 0x10);
            String keyfile = fetchKey(stamp, 0xB4AD4);
            boolean badKey = keyfile.equals("");
            if (badKey)
            {
                if (showUI)
                {
                    L_KeySAV.setText("Key not found. Please break for this SAV first.");
                    B_GoSAV.setEnabled(false);
                }
                return 0;
            }
            else
            {
                if (showUI)
                {
                    B_GoSAV.setEnabled(true);
                    L_KeySAV.setText(new File(keyfile).getName());
                }
                savkeypath = keyfile;
            }
            if (showUI)
            {
                B_GoSAV.setEnabled(!badKey);
                CB_BoxEnd.setEnabled(!badKey);
                CB_BoxStart.setEnabled(!badKey);
                B_BKP_SAV.setEnabled(!badKey);
            }
            byte[] key = Files.readAllBytes(Paths.get(keyfile));
            byte[] empty = new byte[232];

            // Save file is already loaded.
            // If slot one was used for the last save copy the boxes to slot 2 and apply key
            if(BitConverter.ToUInt32(key, 0x80000) == BitConverter.ToUInt32(savefile, 0x168))
            {
                int boxoffset = BitConverter.ToInt32(key, 0x1C);
                for(int i = 0, j = boxoffset; i<232*30*31; ++i, ++j)
                {
                    savefile[j] = (byte)(savefile[j - 0x7F000] ^ key[0x80004 + i]);
                }
            }

            // Get our empty file set up.
            System.arraycopy(key, 0x10, empty, 0xE0, 0x4);
            String nick = eggnames[empty[0xE3] - 1];
            // Stuff in the nickname to our blank EKX.
            byte[] nicknamebytes = nick.getBytes("UTF-16LE");
            nicknamebytes = Arrays.copyOf(nicknamebytes, 24);
            System.arraycopy(nicknamebytes, 0, empty, 0x40, nicknamebytes.length);
            // Fix CHK
            int chk = 0;
            for (int i = 8; i < 232; i += 2) // Loop through the entire PKX
                chk += BitConverter.ToUInt16(empty, i);
            // Apply New Checksum
            System.arraycopy(BitConverter.GetBytes(chk), 0, empty, 06, 2);
            empty = encryptArray(empty);
            empty = Arrays.copyOf(empty, 0xE8);

            // Scan the save and update keys
            scanSAV(savefile, key, empty, showUI);
            Files.write(Paths.get(keyfile), key); // Key has been scanned for new slots, re-save key.
            return 1;
        }
        catch (IOException e) { JOptionPane.showMessageDialog(this, "Error reading selected file, reading keystream file, or writing keystream file.\n\n" + e, "Error", JOptionPane.ERROR_MESSAGE); return 0;}
    }

    private void openBIN(File path)
    {
        try
        {
            // File size already checked, so we're good to "Go"; load it in to RAM
            byte[] input = Files.readAllBytes(path.toPath());
            System.arraycopy(input, 0, savefile, 0, input.length);
            TB_SAV.setText(path.getPath());
            L_KeySAV.setText("Decrypted; no key neeeded.");
            CB_BoxEnd.setEnabled(true);
            CB_BoxStart.setEnabled(true);
            B_BKP_SAV.setEnabled(true);
            B_GoSAV.setEnabled(true);
        }
        catch (IOException e) { JOptionPane.showMessageDialog(this, "Error reading selected file.\n\n" + e, "Error", JOptionPane.ERROR_MESSAGE); }
    }

    private void openVID(File path)
    {
        try
        {
            // check to see if good input file
            B_GoBV.setEnabled(false);
            CB_Team.setEnabled(false);
            long len = path.length();
            if (len != 28256)
            {
                JOptionPane.showMessageDialog(this, "Incorrect File Loaded: Not a Battle Video (~27.5KB).", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            TB_BV.setText(path.getPath());

            // Go ahead and load the save file into RAM...
            batvideo = Files.readAllBytes(path.toPath());

            // Fetch Stamp
            long stamp = BitConverter.ToUInt64(batvideo, 0x10);
            String keyfile = fetchKey(stamp, 0x1000);
            boolean badKey = keyfile.equals("");
            B_GoBV.setEnabled(!badKey);
            CB_Team.setEnabled(!badKey);
            B_BKP_BV.setEnabled(!badKey);
            if (badKey)
            {
                L_KeyBV.setText("Key not found. Please break for this BV first.");
                return;
            }
            else
            {
                L_KeyBV.setText(new File(keyfile).getName());
                vidkeypath = keyfile;
            }

            // Check up on the key file...
            CB_Team.removeAllItems();
            CB_Team.addItem("My Team");
            byte[] bvkey = Files.readAllBytes(Paths.get(vidkeypath));
            if (BitConverter.ToUInt64(bvkey, 0x800) != 0)
                CB_Team.addItem("Enemy Team");
            CB_Team.setSelectedIndex(0);
        }
        catch (IOException e) { JOptionPane.showMessageDialog(this, "Error reading selected file and/or keystream file.\n\n" + e, "Error", JOptionPane.ERROR_MESSAGE); }
    }

    private String fetchKey(long stamp, int length)
    {
        // Find the Key in the datapath (program//data folder)
        File[] files = new File(datapath).listFiles();
        for (File file : files)
        {
            if (file.length() == length)
            {
                try
                {
                    byte[] data = Files.readAllBytes(file.toPath());
                    long newstamp = BitConverter.ToUInt64(data, 0x0);
                    if (newstamp == stamp)
                        return file.getPath();
                }
                catch (IOException e) { JOptionPane.showMessageDialog(this, "Error reading keystream file.\n\n" + e, "Error", JOptionPane.ERROR_MESSAGE); }
            }
        }
        // else return nothing
        return "";
    }
    
    // File Dumping
    // SAV
    private byte[] fetchpkx(byte[] input, byte[] keystream, int pkxoffset, int key1off, int key2off, byte[] blank)
    {
        // Auto updates the keystream when it dumps important data!
        ghost = true;
        byte[] ekx = new byte[232];
        byte[] key1 = new byte[232]; System.arraycopy(keystream, key1off, key1, 0, 232);
        byte[] key2 = new byte[232]; System.arraycopy(keystream, key2off, key2, 0, 232);
        byte[] encrypteddata = new byte[232]; System.arraycopy(input, pkxoffset, encrypteddata, 0, 232);

        byte[] zeros = new byte[232];
        byte[] ezeros = encryptArray(zeros); ezeros = Arrays.copyOf(ezeros, 0xE8);
        if (Arrays.equals(zeros, key1) && Arrays.equals(zeros, key2))
            return null;
        else if (Arrays.equals(zeros, key1))
        {
            // Key2 is confirmed to dump the data.
            ekx = xortwos(key2, encrypteddata);
            ghost = false;
        }
        else if (Arrays.equals(zeros, key2))
        {
            // Haven't dumped from this slot yet.
            if (Arrays.equals(key1, encrypteddata))
            {
                // Slot hasn't changed.
                return null;
            }
            else
            {
                // Try and decrypt the data...
                ekx = xortwos(key1, encrypteddata);
                if (verifyCHK(decryptArray(ekx)))
                {
                    // Data has been dumped!
                    // Fill keystream data with our log.
                    System.arraycopy(encrypteddata, 0, keystream, key2off, 232);
                }
                else
                {
                    // Try xoring with the empty data.
                    if (verifyCHK(decryptArray(xortwos(ekx, blank))))
                    {
                        ekx = xortwos(ekx, blank);
                        System.arraycopy(xortwos(encrypteddata, blank), 0, keystream, key2off, 232);
                    }
                    else if (verifyCHK(decryptArray(xortwos(ekx, ezeros))))
                    {
                        ekx = xortwos(ekx, ezeros);
                        System.arraycopy(xortwos(encrypteddata, ezeros), 0, keystream, key2off, 232);
                    }
                    else return null; // Not a failed decryption; we just haven't seen new data here yet.
                }
            }
        }
        else
        {
            // We've dumped data at least once.
            if (Arrays.equals(key1, encrypteddata) || Arrays.equals(key1, xortwos(encrypteddata,blank)) || Arrays.equals(key1, xortwos(encrypteddata,ezeros)))
            {
                // Data is back to break state, but we can still dump with the other key.
                ekx = xortwos(key2, encrypteddata);
                if (!verifyCHK(decryptArray(ekx)))
                {
                    if (verifyCHK(decryptArray(xortwos(ekx, blank))))
                    {
                        ekx = xortwos(ekx, blank);
                        System.arraycopy(xortwos(key2, blank), 0, keystream, key2off, 232);
                    }
                    else if (verifyCHK(decryptArray(xortwos(ekx, ezeros))))
                    {
                        // Key1 decrypts our data after we remove encrypted zeros.
                        // Copy Key1 to Key2, then zero out Key1.
                        ekx = xortwos(ekx, ezeros);
                        System.arraycopy(xortwos(key2, ezeros), 0, keystream, key2off, 232);
                    }
                    else return null; // Decryption Error
                }
            }
            else if (Arrays.equals(key2, encrypteddata) || Arrays.equals(key2, xortwos(encrypteddata, blank)) || Arrays.equals(key2, xortwos(encrypteddata, ezeros)))
            {
                // Data is changed only once to a dumpable, but we can still dump with the other key.
                ekx = xortwos(key1, encrypteddata); 
                if (!verifyCHK(decryptArray(ekx)))
                {
                    if (verifyCHK(decryptArray(xortwos(ekx, blank))))
                    {
                        ekx = xortwos(ekx, blank);
                        System.arraycopy(xortwos(key1, blank), 0, keystream, key1off, 232);
                    }
                    else if (verifyCHK(decryptArray(xortwos(ekx, ezeros))))
                    {
                        ekx = xortwos(ekx, ezeros);
                        System.arraycopy(xortwos(key1, ezeros), 0, keystream, key1off, 232);
                    }
                    else return null; // Decryption Error
                }
            }
            else
            {
                // Data has been observed to change twice! We can get our exact keystream now!
                // Either Key1 or Key2 or Save is empty. Whichever one decrypts properly is the empty data.
                // Oh boy... here we go...
                ghost = false;
                boolean keydata1, keydata2;
                byte[] data1 = xortwos(encrypteddata, key1);
                byte[] data2 = xortwos(encrypteddata, key2);

                keydata1 = 
                    (verifyCHK(decryptArray(data1))
                    ||
                    verifyCHK(decryptArray(xortwos(data1, ezeros)))
                    ||
                    verifyCHK(decryptArray(xortwos(data1, blank)))
                    );
                keydata2 = 
                    (verifyCHK(decryptArray(data2))
                    ||
                    verifyCHK(decryptArray(xortwos(data2, ezeros)))
                    ||
                    verifyCHK(decryptArray(xortwos(data2, blank)))
                    );
                if (!keydata1 && !keydata2) 
                    return null; // All 3 are occupied.
                if (keydata1 && keydata2)
                {
                    // Save file is currently empty...
                    // Copy key data from save file if it decrypts with Key1 data properly.

                    if (verifyCHK(decryptArray(data1)))
                    {
                        // No modifications necessary.
                        ekx = data1;
                        System.arraycopy(encrypteddata, 0, keystream, key2off, 232);
                        System.arraycopy(zeros, 0, keystream, key1off, 232);
                    }
                    else if (verifyCHK(decryptArray(xortwos(data1, ezeros))))
                    {
                        ekx = ezeros;
                        System.arraycopy(xortwos(encrypteddata,ezeros), 0, keystream, key2off, 232);
                        System.arraycopy(zeros, 0, keystream, key1off, 232);
                    }
                    else if (verifyCHK(decryptArray(xortwos(data1, blank))))
                    {
                        ekx = ezeros;
                        System.arraycopy(xortwos(encrypteddata, blank), 0, keystream, key2off, 232);
                        System.arraycopy(zeros, 0, keystream, key1off, 232);
                    }
                    else return null; // unreachable
                }
                else if (keydata1) // Key 1 data is empty
                {
                    if (verifyCHK(decryptArray(data1)))
                    {
                        ekx = data1;
                        System.arraycopy(key1, 0, keystream, key2off, 232);
                        System.arraycopy(zeros, 0, keystream, key1off, 232);
                    }
                    else if (verifyCHK(decryptArray(xortwos(data1, ezeros))))
                    {
                        ekx = xortwos(data1, ezeros);
                        System.arraycopy(xortwos(key1, ezeros), 0, keystream, key2off, 232);
                        System.arraycopy(zeros, 0, keystream, key1off, 232);
                    }
                    else if (verifyCHK(decryptArray(xortwos(data1, blank))))
                    {
                        ekx = xortwos(data1, blank);
                        System.arraycopy(xortwos(key1, blank), 0, keystream, key2off, 232);
                        System.arraycopy(zeros, 0, keystream, key1off, 232);
                    }
                    else return null; // unreachable
                }
                else if (keydata2)
                {
                    if (verifyCHK(decryptArray(data2)))
                    {
                        ekx = data2;
                        System.arraycopy(key2, 0, keystream, key2off, 232);
                        System.arraycopy(zeros, 0, keystream, key1off, 232);
                    }
                    else if (verifyCHK(decryptArray(xortwos(data2, ezeros))))
                    {
                        ekx = xortwos(data2, ezeros);
                        System.arraycopy(xortwos(key2, ezeros), 0, keystream, key2off, 232);
                        System.arraycopy(zeros, 0, keystream, key1off, 232);
                    }
                    else if (verifyCHK(decryptArray(xortwos(data2, blank))))
                    {
                        ekx = xortwos(data2, blank);
                        System.arraycopy(xortwos(key2, blank), 0, keystream, key2off, 232);
                        System.arraycopy(zeros, 0, keystream, key1off, 232);
                    }
                    else return null; // unreachable
                }
            }
        }
        byte[] pkx = decryptArray(ekx);
        if (verifyCHK(pkx))
        {
            slots++;
            return pkx;
        }
        else 
            return null; // Slot Decryption error?!
    }

    private void scanSAV(byte[] input, byte[] keystream, byte[] blank, boolean showUI)
    {
        slots = 0;
        int boxoffset = BitConverter.ToInt32(keystream, 0x1C);
        for (int i = 0; i < 930; i++)
            fetchpkx(input, keystream, boxoffset + i * 232, 0x100 + i * 232, 0x40000 + i * 232, blank);
        if(showUI)
            L_SAVStats.setText(String.format("{0}/930", slots));
    }
    
    private void dumpPKX(boolean isSAV, byte[] pkx, int dumpnum, int dumpstart)
    {
        if (isSAV && ghost && CHK_HideFirst.isSelected()) return;
        if (pkx == null || !verifyCHK(pkx))
            return;
        Structures.PKX data = new Structures.PKX(pkx);

        // Printout Parsing
        if (data.species == 0)
            return;

        // Unicode stuff
        String checkmark = (CHK_Unicode.isSelected()) ? "✓" : "#";
        String shinymark = (CHK_Unicode.isSelected()) ? "★" : "*";
        String gen6mark = (CHK_Unicode.isSelected()) ? "⬟" : "#";
        String femalemark = (CHK_Unicode.isSelected()) ? "♀" : "F";
        String malemark = (CHK_Unicode.isSelected()) ? "♂" : "M";

        // Parse Pokemon info
        String box = (isSAV) ? String.format("B%02d",dumpstart + (dumpnum/30)) : "-";
        String slot = (isSAV) ? String.format("%d,%d", (dumpnum%30) / 6 + 1, dumpnum % 6 + 1) : Integer.toString(dumpnum);
        String species = specieslist[data.species];
        String gender;
        if (data.genderflag == 0)
            gender = malemark;
        else if (data.genderflag == 1)
            gender = femalemark;
        else gender = "-";
        String nature = natures[data.nature];
        String ability = abilitylist[data.ability];
        String hiddenability = (data.abilitynum == 4) ? checkmark : "";
        String hp = Integer.toString(data.HP_IV);
        String atk = Integer.toString(data.ATK_IV);
        String def = Integer.toString(data.DEF_IV);
        String spa = Integer.toString(data.SPA_IV);
        String spd = Integer.toString(data.SPD_IV);
        String spe = Integer.toString(data.SPE_IV);
        String hptype = types[data.hptype];
        String ESV = String.format("%04d", data.ESV);
        String TSV = String.format("%04d", data.TSV);
        String ball = balls[data.ball];
        String ballimg = "[](/" + ball.replace(" ", "").replace("é", "e").toLowerCase() + ")";
        String nickname = data.nicknamestr;
        String otname = data.ot;
        String TID = String.format("%05d", data.TID);
        String SID = String.format("%05d", data.SID);
        String ev_hp = Integer.toString(data.HP_EV);
        String ev_at = Integer.toString(data.ATK_EV);
        String ev_de = Integer.toString(data.DEF_EV);
        String ev_sa = Integer.toString(data.SPA_EV);
        String ev_sd = Integer.toString(data.SPD_EV);
        String ev_se = Integer.toString(data.SPE_EV);
        String number = (isSAV) ? Integer.toString(dumpnum % 30 + 1) : slot;
        String overallCount = Integer.toString(dumpedcounter+1);
        String isshiny = (data.isshiny) ? shinymark : "";
        String isegg = (data.isegg) ? checkmark : "";

        // Handle bad decryption on moves
        String move1;
        String move2;
        String move3;
        String move4;
        String relearn1;
        String relearn2;
        String relearn3;
        String relearn4;
        try { move1 = movelist[data.move1]; } catch (Exception e) { move1 = "ERROR"; }
        try { move2 = movelist[data.move2]; } catch (Exception e) { move2 = "ERROR"; }
        try { move3 = movelist[data.move3]; } catch (Exception e) { move3 = "ERROR"; }
        try { move4 = movelist[data.move4]; } catch (Exception e) { move4 = "ERROR"; }
        try { relearn1 = movelist[data.eggmove1]; } catch (Exception e) { relearn1 = "ERROR"; }
        try { relearn2 = movelist[data.eggmove2]; } catch (Exception e) { relearn2 = "ERROR"; }
        try { relearn3 = movelist[data.eggmove3]; } catch (Exception e) { relearn3 = "ERROR"; }
        try { relearn4 = movelist[data.eggmove4]; } catch (Exception e) { relearn4 = "ERROR"; }

        // Extra fields for CSV custom output
        // TODO: add an option to use actual markers below instead of numbers
        // String[] markers = new String[] { "●", "▲", "■", "♥", "★", "♦" };
        int IVcounter = 0;
        String hpm = ""; if (data.HP_IV == 31) { hpm = "1"; IVcounter++; }
        String atkm = ""; if (data.ATK_IV == 31) { atkm = "2"; IVcounter++; }
        String defm = ""; if (data.DEF_IV == 31) { defm = "3"; IVcounter++; }
        String spam = ""; if (data.SPA_IV == 31) { spam = "4"; IVcounter++; }
        String spdm = ""; if (data.SPD_IV == 31) { spdm = "5"; IVcounter++; }
        String spem = ""; if (data.SPE_IV == 31) { spem = "6"; IVcounter++; }
        String IVs = (IVcounter == 1) ? String.format("%d IV", IVcounter) : String.format("%d IVs", IVcounter);
        String IVsum = Integer.toString(data.HP_IV + data.ATK_IV + data.DEF_IV + data.SPA_IV + data.SPD_IV + data.SPE_IV);
        String EVsum = Integer.toString(data.HP_EV + data.ATK_EV + data.DEF_EV + data.SPA_EV + data.SPD_EV + data.SPE_EV);
        String eggDate = (Integer.toString(data.egg_year).equals("0")) ? "" : String.format("20%02d-%02d-%02d", data.egg_year, data.egg_month, data.egg_day);
        String metDate = (data.isegg) ? "" : String.format("20%02d-%02d-%02d", data.met_year, data.met_month, data.met_day);
        String experience = Long.toString(data.exp);
        String level = (data.isegg) ? "" : Integer.toString(getLevel(data.species, data.exp));
        String region = regionList[data.gamevers];
        String pgame = gameList[data.gamevers];
        String country = countryList[data.countryID];
        String helditem = (data.helditem == 0) ? "" : itemlist[data.helditem];
        String language = languageList[data.otlang];
        String mark = (data.gamevers >= 24 && data.gamevers <= 27) ? gen6mark : ""; // Mark is for Gen 6 Pokemon, so X Y OR AS
        String PID = Long.toString(data.PID);
        String dex = Integer.toString(data.species);
        String form = Integer.toString(data.altforms);
        String pkrsInfected = (data.PKRS_Strain > 0) ? checkmark : "";
        String pkrsCured = (data.PKRS_Strain > 0 && data.PKRS_Duration == 0) ? checkmark : "";
        String OTgender = (data.otgender == 1) ? femalemark : malemark;
        String metLevel = Integer.toString(data.metlevel);
        String OTfriendship = Integer.toString(data.OTfriendship);
        String OTaffection = Integer.toString(data.OTaffection);
        String stepsToHatch = (!data.isegg) ? "" : Integer.toString((data.OTfriendship * 255));

        // Do the Filtering
        boolean satisfiesFilters = true;
        if (isSAV)
        {
            while (CHK_Enable_Filtering.isSelected())
            {
                if (CHK_Egg.isSelected() && !data.isegg) { satisfiesFilters = false; break; }

                if (CHK_Has_HA.isSelected() && data.abilitynum != 4) { satisfiesFilters = false; break; }

                if (CB_Abilities.getSelectedItem() != "" && CB_Abilities.getSelectedIndex() != 0 && CB_Abilities.getSelectedItem() != ability)
                { satisfiesFilters = false; break; }

                boolean checkHP = CCB_HPType.getModel().isChecked(CCB_HPType.getModel().getElementAt(0));
                byte checkHPDiff = (checkHP) ? (byte)1 : (byte)0;
                int perfects = CB_No_IVs.getSelectedIndex();
                for (Object[] iv : new Object[][] {
                    {data.HP_IV, CHK_IV_HP.isSelected()}, 
                    {data.DEF_IV, CHK_IV_Def.isSelected()}, 
                    {data.SPA_IV, CHK_IV_SpAtk.isSelected()}, 
                    {data.SPD_IV, CHK_IV_SpDef.isSelected()} })
                {
                    if (31 - (int)iv[0] <= checkHPDiff) --perfects;
                    else if ((boolean)iv[1]) { satisfiesFilters = false; break; }
                }
                for (Object[] iv : new Object[][] {
                    {data.ATK_IV, CHK_IV_Atk.isSelected(), CHK_Special_Attacker.isSelected()}, 
                    {data.SPE_IV, CHK_IV_Spe.isSelected(), CHK_Trickroom.isSelected()} })
                {
                    if (Math.abs(((boolean)iv[2] ? 0: 31) - (int)iv[0]) <= checkHPDiff) --perfects;
                    else if ((boolean)iv[1]) { satisfiesFilters = false; break; }
                }

                if(perfects > 0) { satisfiesFilters = false; break; }

                if(checkHP && !CCB_HPType.getModel().isChecked(types[data.hptype])) { satisfiesFilters = false; break; }

                if(!CCB_Natures.getModel().isChecked(natures[data.nature])) { satisfiesFilters = false; break; }

                if (CHK_Is_Shiny.isSelected() || CHK_Hatches_Shiny_For_Me.isSelected() || CHK_Hatches_Shiny_For.isSelected())
                {
                    if (!(CHK_Is_Shiny.isSelected() && data.isshiny ||
                        data.isegg && CHK_Hatches_Shiny_For_Me.isSelected() && ESV.equals(TSV) ||
                        data.isegg && CHK_Hatches_Shiny_For.isSelected() && selectedTSVs.contains(data.ESV)))
                    { satisfiesFilters = false; break; }
                }

                if(RAD_Male.isSelected() && data.genderflag != 0 || RAD_Female.isSelected() && data.genderflag != 1)
                { satisfiesFilters = false; break; }

                break;
            }
        }

        // If it satisfies filters or we're doing a battle video, print it out
        if (satisfiesFilters || !isSAV)
        {
            if (!data.isegg && !CHK_ShowESV.isSelected()) ESV = "";

            // Vivillon Forms
            if (data.species >= 664 && data.species <= 666)
                species += "-" + vivlist[data.altforms];

            // Unown Forms
            if (data.species == 201)
                species += "-" + unownlist[data.altforms];

            // Bold the IVs if Reddit and option is checked
            if (CB_ExportStyle.getSelectedIndex() >= 1 && CB_ExportStyle.getSelectedIndex() <= 5 && CHK_BoldIVs.isSelected())
            {
                if (hp.equals("31")) hp = "**31**";
                if (atk.equals("31")) atk = "**31**";
                if (def.equals("31")) def = "**31**";
                if (spa.equals("31")) spa = "**31**";
                if (spd.equals("31")) spd = "**31**";
                if (spe.equals("31")) spe = "**31**";
            }

            // Get the output format from the input text box
            String format = RTB_OPTIONS.getText();

            // For PK6 output, display default format and output PK6 files
            if (CB_ExportStyle.getSelectedIndex() == 8)
            {
                format = "{0} - {1} - {2} ({3}) - {4} - {5} - {6}.{7}.{8}.{9}.{10}.{11} - {12} - {13}";
                isshiny = (data.isshiny) ? " " + shinymark : "";

                // For nicknamed Pokemon, append the species name to the file name
                if (data.isnick)
                    data.nicknamestr += String.format(" (%s)", specieslist[data.species]);
                String savedname = String.format("%02d - %03d%s %s - %s%s", (dumpnum % 30 + 1), data.species, isshiny, data.nicknamestr, Integer.toHexString(data.chk), Long.toHexString(data.EC));
                if (isSAV) savedname = box + " " + savedname;
                try { Files.write(Paths.get(dbpath, CleanFileName(savedname), ".pk6"), pkx); }
                catch (IOException e) { JOptionPane.showMessageDialog(this, "Error writing PK6 file.\n\n" + e, "Error", JOptionPane.ERROR_MESSAGE); }
            }

            // Add brackets to ESV for defaults (0 and 8) and custom (3-5) if Table is NOT checked
            if (CB_ExportStyle.getSelectedIndex() == 0 || CB_ExportStyle.getSelectedIndex() == 8 || (!CHK_R_Table.isSelected() && CB_ExportStyle.getSelectedIndex() >= 3 && CB_ExportStyle.getSelectedIndex() <= 5))
            {
                if (!ESV.equals(""))
                    ESV = "[" + ESV + "]";
            }

            // Escape any quotes so we can add quotes in the CSV export to avoid errors with commas in nicknames and trainer names
            if (CHK_NameQuotes.isSelected() && (CB_ExportStyle.getSelectedIndex() == 6 || CB_ExportStyle.getSelectedIndex() == 7))
            {
                nickname = "\"" + nickname.replace("\"", "\\\"") + "\"";
                otname = "\"" + otname.replace("\"", "\\\"") + "\"";
            }

            // Generate result for this Pokemon
            String result = String.format(format, box, slot, species, gender, nature, ability, hp, atk, def, spa, spd, spe, hptype, ESV, TSV, nickname, otname, ball, TID, SID, ev_hp, ev_at, ev_de, ev_sa, ev_sd, ev_se, move1, move2, move3, move4, relearn1, relearn2, relearn3, relearn4, isshiny, isegg, level, region, country, helditem, language, pgame, number, PID, mark, dex, form, hpm, atkm, defm, spam, spdm, spem, IVs, IVsum, EVsum, eggDate, metDate, experience, overallCount, pkrsInfected, pkrsCured, OTgender, metLevel, OTfriendship, OTaffection, stepsToHatch, ballimg, hiddenability);

            // Add the result to the CSV data if needed
            if (CB_ExportStyle.getSelectedIndex() == 6 || CB_ExportStyle.getSelectedIndex() == 7)
                csvdata += result + "\r\n";

            if (isSAV && ghost && CHK_MarkFirst.isSelected()) result = "~" + result;
            dumpedcounter++;
            if (isSAV) RTB_SAV.append(result + "\n"); else RTB_VID.append(result + "\n");
        }
    }
    
    private void dumpSAV(java.awt.event.ActionEvent evt)
    {
        dumpData(true);
    }

    private void dumpBV(java.awt.event.ActionEvent evt)
    {
        dumpData(false);
    }

    private void dumpData(boolean isSAV)
    {
        // Get the output format from the input text box
        String format = RTB_OPTIONS.getText();

        // For PK6 output, display default format
        if (CB_ExportStyle.getSelectedIndex() == 8)
            format = "{0} - {1} - {2} ({3}) - {4} - {5} - {6}.{7}.{8}.{9}.{10}.{11} - {12} - {13}";

        // Get the header
        String header = getHeaderString(format, isSAV);
        if (CHK_Header.isSelected()) csvdata = header + "\n";

        // Add header if Reddit, or if custom and Reddit table checked
        String toAppend = "";
        if (CB_ExportStyle.getSelectedIndex() == 1 || CB_ExportStyle.getSelectedIndex() == 2 || (CB_ExportStyle.getSelectedIndex() >= 1 && CB_ExportStyle.getSelectedIndex() <= 5 && CHK_R_Table.isSelected()))
        {
            int args = RTB_OPTIONS.getText().split("\\{").length;
            header += "\n|";
            for (int i = 0; i < args; i++)
                header += ":---:|";

            // Still append the header if we aren't doing it for every box.
            if (!CHK_Split.isSelected() || !isSAV)
            {
                // Add header if reddit
                if (CHK_ColorBox.isSelected())
                {
                    if (CB_BoxColor.getSelectedIndex() == 0)
                        toAppend += boxcolors[1 + (rnd32() % 4)];
                    else
                        toAppend += boxcolors[CB_BoxColor.getSelectedIndex() - 1];
                }

                // Append Box Name then Header
                if (isSAV)
                    toAppend += (CB_BoxStart.getSelectedItem() == "All") ? "All Boxes" : ((CB_BoxStart.getSelectedItem() == CB_BoxEnd.getSelectedItem()) ? "Box " + CB_BoxStart.getSelectedItem() : "Boxes " + CB_BoxStart.getSelectedItem() + " to " + CB_BoxEnd.getSelectedItem());
                else
                    toAppend += (CB_Team.getSelectedIndex() == 1) ? "Enemy Team" : "My Team";
                toAppend += "\n\n";
                if (CHK_Header.isSelected()) toAppend += header + "\n";
            }
        }

        // Print out header at least once if "Split Boxes" is not checked
        else if ((!CHK_Split.isSelected() || !isSAV) && CHK_Header.isSelected())
            toAppend += header + "\n";

        // Dump the actual Pokemon data for saves
        if (isSAV)
        {
            RTB_SAV.setText("");
            dumpedcounter = 0;
            int boxoffset = 0;
            byte[] keystream = new byte[0xB4AD4];
            byte[] empty = new byte[232];
            if (binType.equals("sav"))
            {
                try
                {
                    // Load our Keystream file.
                    keystream = Files.readAllBytes(Paths.get(savkeypath));
                    // Save file is already loaded.
                }
                catch (IOException e) { JOptionPane.showMessageDialog(this, "Error reading keystream file.\n\n" + e, "Error", JOptionPane.ERROR_MESSAGE); return; }

                // Get our empty file set up.
                System.arraycopy(keystream, 0x10, empty, 0xE0, 0x4);
                String nick = eggnames[empty[0xE3] - 1];
                // Stuff in the nickname to our blank EKX.
                byte[] nicknamebytes = new byte[20];
                try
                {
                    nicknamebytes = nick.getBytes("UTF-16LE");
                }
				catch (UnsupportedEncodingException e) { JOptionPane.showMessageDialog(this, "Error occurred during nickname stuffing.\n\n" + e, "Error", JOptionPane.ERROR_MESSAGE); }
                nicknamebytes = Arrays.copyOf(nicknamebytes, 24);
                System.arraycopy(nicknamebytes, 0, empty, 0x40, nicknamebytes.length);
                // Fix CHK
                int chk = 0;
                for (int i = 8; i < 232; i += 2) // Loop through the entire PKX
                    chk += BitConverter.ToUInt16(empty, i);

                // Apply New Checksum
                System.arraycopy(BitConverter.GetBytes(chk), 0, empty, 06, 2);
                empty = encryptArray(empty);
                empty = Arrays.copyOf(empty, 0xE8);
                boxoffset = BitConverter.ToInt32(keystream, 0x1C);
            }

            // Set our box data offset based on where the file came from
            int binOffset = 0;
            switch (binType)
            {
                case "sav":
                    // Offset is already 0
                    break;

                case "yabd":
                    binOffset = 4;
                    byte[] test = new byte[232];
                    System.arraycopy(savefile, binOffset, test, 0, 232);
                    if (!verifyCHK(decryptArray(test)))
                        binOffset = 8;
                    break;

                case "xy":
                    binOffset = 0x1EF38;
                    break;

                case "oras":
                    binOffset = 0x2F794;
                    byte[] test2 = new byte[232];
                    System.arraycopy(savefile, binOffset, test2, 0, 232);
                    if (!verifyCHK(decryptArray(test2)))
                        binOffset = 0x1EF38;
                    break;
            }

            // Get our dumping parameters.
            int offset = 0;
            int count;
            int boxstart = 1;
            if (CB_BoxStart.getSelectedItem() == "All")
                count = 30 * 31;
            else
            {
                boxoffset += (Integer.parseInt((String)CB_BoxStart.getSelectedItem()) - 1) * 30 * 232;
                offset += (Integer.parseInt((String)CB_BoxStart.getSelectedItem()) - 1) * 30 * 232;
                count = (Integer.parseInt((String)CB_BoxEnd.getSelectedItem()) - Integer.parseInt((String)CB_BoxStart.getSelectedItem()) + 1) * 30;
                boxstart = Integer.parseInt((String)CB_BoxStart.getSelectedItem());
            }

            // Get our TSVs for filtering
            short tmp = 0;
            for (String line : TB_SVs.getText().split("\\s*[\\s,;.]\\s*"))
            {
                try
                {
                    selectedTSVs.add(Short.parseShort(line));
                } catch (NumberFormatException e) { }
            }

            // Print out the header (if any) and loop through selected boxes
            RTB_SAV.append(toAppend);
            for (int i = 0; i < count; i++)
            {
                if (i % 30 == 0 && CHK_Split.isSelected())
                {
                    if (i != 0) RTB_SAV.append("\n");

                    // Add Reddit coloring
                    if (CHK_ColorBox.isSelected() && (CB_ExportStyle.getSelectedIndex() == 1 || CB_ExportStyle.getSelectedIndex() == 2 || (CB_ExportStyle.getSelectedIndex() >= 1 && CB_ExportStyle.getSelectedIndex() <= 5 && CHK_R_Table.isSelected())))
                    {
                        if (CB_BoxColor.getSelectedIndex() == 0)
                            RTB_SAV.append(boxcolors[1 + ((i / 30 + boxstart) % 4)]);
                        else
                            RTB_SAV.append(boxcolors[CB_BoxColor.getSelectedIndex() - 1]);
                    }

                    // Append Box Name then Header
                    RTB_SAV.append("Box " + Integer.toString(i / 30 + boxstart) + "\n\n");
                    if (CHK_Header.isSelected()) RTB_SAV.append(header + "\n");
                }

                // Get the pkx and dump it
                byte[] pkx = new byte[232];
                if (binType.equals("sav"))
                    pkx = fetchpkx(savefile, keystream, boxoffset + i * 232, 0x100 + offset + i * 232, 0x40000 + offset + i * 232, empty);
                else
                {
                    System.arraycopy(savefile, binOffset + boxoffset + i * 232, pkx, 0, 232);
                    pkx = decryptArray(pkx);
                }
                dumpPKX(true, pkx, i, boxstart);
            }
        }

        // Dump the Pokemon data for videos
        else
        {
            try
            {
                RTB_VID.setText("");
                // player @ 0xX100, opponent @ 0x1800;
                byte[] keystream = Files.readAllBytes(Paths.get(vidkeypath));
                byte[] key = new byte[260];
                byte[] empty = new byte[260];
                byte[] emptyekx;
                byte[] ekx = new byte[260];
                int offset = 0x4E18;
                int keyoff = 0x100;
                if (CB_Team.getSelectedIndex() == 1)
                {
                    offset = 0x5438;
                    keyoff = 0x800;
                }
                RTB_VID.append(toAppend);
                for (int i = 0; i < 6; i++)
                {
                    System.arraycopy(batvideo, offset + 260 * i, ekx, 0, 260);
                    System.arraycopy(keystream, keyoff + 260 * i, key, 0, 260);
                    ekx = xortwos(ekx, key);
                    if (verifyCHK(decryptArray(ekx)))
                        dumpPKX(false, decryptArray(ekx), i+1, 0);
                    else
                        dumpPKX(false, null, i, 0);
                }
            }
            catch (IOException e) { JOptionPane.showMessageDialog(this, "Error reading keystream file.\n\n" + e, "Error", JOptionPane.ERROR_MESSAGE); return; }
        }

        // Copy Results to Clipboard
        if (isSAV)
        {
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(RTB_SAV.getText()), null);
            RTB_SAV.append("\nData copied to clipboard!\nDumped: " + dumpedcounter);
            RTB_SAV.select(0, RTB_SAV.getText().length() - 1);
            // RTB_SAV.ScrollToCaret();
        }
        else
        {
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(RTB_VID.getText()), null);
            RTB_VID.append("\nData copied to clipboard!"); 
            RTB_VID.select(0, RTB_VID.getText().length() - 1);
            // RTB_VID.ScrollToCaret();
        }

        // Write the CSV file if selected
        if (CB_ExportStyle.getSelectedIndex() == 6 || CB_ExportStyle.getSelectedIndex() == 7)
        {
            JFileChooser savecsv = new JFileChooser((isSAV) ? savpath : vidpath);
            String theName = (lastOpenedFilename.equals("")) ? "KeySAV Data Dump.csv" : lastOpenedFilename.substring(0, -4) + ".csv";
            File suggested = new File(((isSAV) ? savpath : vidpath) + File.pathSeparator + theName);
            savecsv.addChoosableFileFilter(new FileNameExtensionFilter("Spreadsheet", "csv"));
            if (savecsv.showSaveDialog(this) == JFileChooser.APPROVE_OPTION)
            {
                File file = savecsv.getSelectedFile();
                List<String> csvdataw = new ArrayList();
                csvdataw.add(csvdata);
                try { Files.write(file.toPath(), csvdataw, Charset.forName("UTF-16LE")); }
                catch (IOException e) { JOptionPane.showMessageDialog(this, "Error writing CSV file.\n\n" + e, "Error", JOptionPane.ERROR_MESSAGE); }
            }
        }
    }
    
    // Hide/show the Filtering group
    private void toggleFilter(java.awt.event.ItemEvent evt)
    {
        GB_Filter.setVisible(CHK_Enable_Filtering.isSelected());
    }
    
    // Check sizes of break files
    private void loadBreak1(java.awt.event.ActionEvent evt)
    {
        // Open Save File
        JFileChooser boxsave = new JFileChooser(savpath);
        boxsave.addChoosableFileFilter(new FileNameExtensionFilter("Save", "sav", "bin"));
        if (boxsave.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
        {
            try
            {
                File path = boxsave.getSelectedFile();
                byte[] input = Files.readAllBytes(path.toPath());
                if (input.length == 0x100000 || input.length == 0x10009C || input.length == 0x10019A)
                {
                    System.arraycopy(input, input.length % 0x100000, break1, 0, 0x100000);
                    Files.write(Paths.get("C:\\Users\\Thomas\\Desktop\\temp"), break1);
                    TB_File1.setText(path.getPath());
                    savpath = boxsave.getCurrentDirectory().toString();
                }
                else
                    JOptionPane.showMessageDialog(this, "Incorrect File Loaded: Not a SAV file (1MB).", "Error", JOptionPane.ERROR_MESSAGE);
            }
            catch (IOException e) { JOptionPane.showMessageDialog(this, "Ini config file saving failed.\n\n" + e, "Error", JOptionPane.ERROR_MESSAGE); }
        } 
        togglebreak();
    }

    private void loadBreakBV1(java.awt.event.ActionEvent evt)
    {
        // Open Video File
        JFileChooser boxsave = new JFileChooser(vidpath);
        boxsave.addChoosableFileFilter(new FileNameExtensionFilter("Battle Video", "*"));
        if (boxsave.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
        {
            try
            {
                File path = boxsave.getSelectedFile();
                byte[] input = Files.readAllBytes(path.toPath());
                if (input.length == 28256)
                {
                    System.arraycopy(input, 0, video1, 0, 28256);
                    TB_FileBV1.setText(path.getPath());
                    vidpath = boxsave.getCurrentDirectory().toString();
                }
                else
                    JOptionPane.showMessageDialog(this, "Incorrect File Loaded: Not a Battle Video (~27.5KB).", "Error", JOptionPane.ERROR_MESSAGE);
            }
            catch (IOException e) { JOptionPane.showMessageDialog(this, "Ini config file saving failed.\n\n" + e, "Error", JOptionPane.ERROR_MESSAGE); }
        } 
        togglebreakBV();
    }

    private void loadBreak2(java.awt.event.ActionEvent evt)
    {
        // Open Save File
        JFileChooser boxsave = new JFileChooser(savpath);
        boxsave.addChoosableFileFilter(new FileNameExtensionFilter("Save", "sav", "bin"));
        if (boxsave.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
        {
            try
            {
                File path = boxsave.getSelectedFile();
                byte[] input = Files.readAllBytes(path.toPath());
                if (input.length == 0x100000 || input.length == 0x10009C || input.length == 0x10019A)
                {
                    System.arraycopy(input, input.length % 0x100000, break2, 0, 0x100000); // Force save to 0x100000
                    TB_File2.setText(path.getPath());
                    savpath = boxsave.getCurrentDirectory().toString();
                }
                else
                    JOptionPane.showMessageDialog(this, "Incorrect File Loaded: Not a SAV file (1MB).", "Error", JOptionPane.ERROR_MESSAGE);
            }
            catch (IOException e) { JOptionPane.showMessageDialog(this, "Ini config file saving failed.\n\n" + e, "Error", JOptionPane.ERROR_MESSAGE); }
        }
        togglebreak();
    }

    private void loadBreakBV2(java.awt.event.ActionEvent evt)
    {
        // Open Video File
        JFileChooser boxsave = new JFileChooser(vidpath);
        boxsave.addChoosableFileFilter(new FileNameExtensionFilter("Battle Video", "*"));
        if (boxsave.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
        {
            try
            {
                File path = boxsave.getSelectedFile();
                byte[] input = Files.readAllBytes(path.toPath());
                if (input.length == 28256)
                {
                    System.arraycopy(input, 0, video2, 0, 28256);
                    TB_FileBV2.setText(path.getPath());
                    vidpath = boxsave.getCurrentDirectory().toString();
                }
                else
                    JOptionPane.showMessageDialog(this, "Incorrect File Loaded: Not a Battle Video (~27.5KB).", "Error", JOptionPane.ERROR_MESSAGE);
            }
            catch (IOException e) { JOptionPane.showMessageDialog(this, "Ini config file saving failed.\n\n" + e, "Error", JOptionPane.ERROR_MESSAGE); }
        }
        togglebreakBV();
    }

    private void loadBreak3(java.awt.event.ActionEvent evt)
    {
        // Open Save File
        JFileChooser boxsave = new JFileChooser(savpath);
        boxsave.addChoosableFileFilter(new FileNameExtensionFilter("Save", "sav", "bin"));
        if (boxsave.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
        {
            try
            {
                File path = boxsave.getSelectedFile();
                byte[] input = Files.readAllBytes(path.toPath());
                if (input.length == 0x100000 || input.length == 0x10009C || input.length == 0x10019A)
                {
                    System.arraycopy(input, input.length % 0x100000, break3, 0, 0x100000); // Force save to 0x100000
                    TB_File3.setText(path.getPath());
                    savpath = boxsave.getCurrentDirectory().toString();
                }
                else
                    JOptionPane.showMessageDialog(this, "Incorrect File Loaded: Not a SAV file (1MB).", "Error", JOptionPane.ERROR_MESSAGE);
            }
            catch (IOException e) { JOptionPane.showMessageDialog(this, "Ini config file saving failed.\n\n" + e, "Error", JOptionPane.ERROR_MESSAGE); }
        }
        togglebreak();
    }

    // Enable Break button if all files are loaded
    private void togglebreak()
    {
        B_Break.setEnabled(false);
        if (!TB_File1.getText().equals("") && !TB_File2.getText().equals("") && !TB_File3.getText().equals(""))
            B_Break.setEnabled(true);
    }

    private void togglebreakBV()
    {
        B_BreakBV.setEnabled(false);
        if (!TB_FileBV1.getText().equals("") && !TB_FileBV2.getText().equals(""))
            B_BreakBV.setEnabled(true);
    }

    // Enable break button if folder is selected
    private void loadBreakFolder(java.awt.event.ActionEvent evt)
    {
        JFileChooser folder = new JFileChooser(savpath);
        folder.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (folder.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
        {
            TB_Folder.setText(folder.getSelectedFile().getPath());
            B_BreakFolder.setEnabled(true);
        }
    }
    
    // This is where the battle video magic happens
    private void breakBV(java.awt.event.ActionEvent evt)
    {
        // Do Trick
        byte[] ezeros = encryptArray(new byte[260]);
        byte[] xorstream = new byte[260 * 6];
        byte[] breakstream = new byte[260 * 6];
        byte[] bvkey = new byte[0x1000];

        // Validity Check to see what all is participating...
        System.arraycopy(video1, 0x4E18, breakstream, 0, 260 * 6);

        // XOR them together at party offset
        for (int i = 0; i < (260 * 6); i++)
            xorstream[i] = (byte)(breakstream[i] ^ video2[i + 0x4E18]);

        // Retrieve EKX_1's data
        byte[] ekx1 = new byte[260];
        for (int i = 0; i < (260); i++)
            ekx1[i] = (byte)(xorstream[i + 260] ^ ezeros[i]);
        for (int i = 0; i < 260; i++)
            xorstream[i] ^= ekx1[i];

        // If old exploit does not properly decrypt slot1...
        byte[] pkx = decryptArray(ekx1);
        if (!verifyCHK(pkx))
        { JOptionPane.showMessageDialog(this, "Improperly set up Battle Videos. Please follow directions and try again", "Error", JOptionPane.ERROR_MESSAGE); return; }

        // Start filling up our key...
        // Copy in the unique CTR encryption data to ID the video...
        System.arraycopy(video1, 0x10, bvkey, 0, 0x10);

        // Copy unlocking data
        byte[] key1 = new byte[260]; System.arraycopy(video1, 0x4E18, key1, 0, 260);
        System.arraycopy(xortwos(ekx1, key1), 0, bvkey, 0x100, 260);
        System.arraycopy(video1, 0x4E18 + 260, bvkey, 0x100 + 260, 260*5); // XORstream from save1 has just keystream.

        // See if Opponent first slot can be decrypted...
        System.arraycopy(video1, 0x5438, breakstream, 0, 260 * 6);

        // XOR them together at party offset
        for (int i = 0; i < (260 * 6); i++)
            xorstream[i] = (byte)(breakstream[i] ^ video2[i + 0x5438]);

        // XOR through the empty data for the encrypted zero data.
        for (int i = 0; i < (260 * 5); i++)
            bvkey[0x100 + 260 + i] ^= ezeros[i % 260];

        // Retrieve EKX_2's data
        byte[] ekx2 = new byte[260];
        for (int i = 0; i < (260); i++)
            ekx2[i] = (byte)(xorstream[i + 260] ^ ezeros[i]);
        for (int i = 0; i < 260; i++)
            xorstream[i] ^= ekx2[i];
        byte[] key2 = new byte[260]; System.arraycopy(video1,0x5438,key2,0,260);
        byte[] pkx2 = decryptArray(ekx2);
        if (verifyCHK(decryptArray(ekx2)) && (BitConverter.ToUInt16(pkx2,0x8) != 0))
        {
            System.arraycopy(xortwos(ekx2,key2), 0, bvkey, 0x800, 260);
            System.arraycopy(video1, 0x5438 + 260, bvkey, 0x800 + 260, 260 * 5); // XORstream from save1 has just keystream.
            for (int i = 0; i < (260 * 5); i++)
                bvkey[0x800 + 260 + i] ^= ezeros[i % 260];
            JOptionPane.showMessageDialog(this, "Can dump from Opponent Data on this key too!");
        }

        // Show some info and save the keystream
        String ot = "";
        try { ot = TrimFromZero(new String(pkx, 0xB0, 24, "UTF-16LE")); }
        catch (UnsupportedEncodingException e) { JOptionPane.showMessageDialog(this, "Error retrieving OT name.\n\n" + e, "Error", JOptionPane.ERROR_MESSAGE); }
        int tid = BitConverter.ToUInt16(pkx, 0xC);
        int sid = BitConverter.ToUInt16(pkx, 0xE);
        int tsv = ((tid ^ sid) >> 4);
        if (JOptionPane.showConfirmDialog(this, String.format("Success!\nYour first Pokemon's TSV: %04d\nOT: %s\n\nClick OK to save your keystream.", tsv, ot), "Prompt", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION)
        {
            Path fi = Paths.get(TB_FileBV1.getText());
            String newFile = CleanFileName(String.format("BV Key - %s.bin", fi.getFileName().toString().split("(-)")[0]));
            Path newPath = Paths.get(path_exe, "data", newFile);
            boolean doit = true;
            if (Files.exists(newPath))
            {
                if (JOptionPane.showConfirmDialog(this, "Keystream already exists!\n\nOverwrite?", "Prompt", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
                {
                    try { Files.delete(newPath); }
                    catch (IOException e) { JOptionPane.showMessageDialog(this, "Error deleting keystream file.\n\n" + e, "Error", JOptionPane.ERROR_MESSAGE);}
                }
                else
                {
                    doit = false;
                    JOptionPane.showMessageDialog(this, "Chose not to save keystream.", "Alert", JOptionPane.WARNING_MESSAGE);
                }
            }
            if (doit)
            {
                try { Files.write(newPath, bvkey); }
                catch (IOException e) { JOptionPane.showMessageDialog(this, "Error writing keystream file.\n\n" + e, "Error", JOptionPane.ERROR_MESSAGE);}
                JOptionPane.showMessageDialog(this, "Keystream saved to file:\n\n" + newPath.toString());
            }
        }
        else
        {
            JOptionPane.showMessageDialog(this, "Chose not to save keystream.", "Alert", JOptionPane.WARNING_MESSAGE);
        }
    }

    // This is where magic happens for encrypted saves!
    private void breakSAV(java.awt.event.ActionEvent evt)
    {
        int[] offset = new int[2];
        byte[] empty = new byte[232];
        byte[] emptyekx = new byte[232];
        byte[] ekxdata = new byte[232];
        byte[] pkx = new byte[232];
        byte[] slotsKey = new byte[0];
        byte[] save1Save = break1;

        // Do Break. Let's first do some sanity checking to find out the 2 offsets we're dumping from.
        // Loop through save file to find
        int fo = savefile.length / 2 + 0x20000; // Initial Offset, can tweak later.
        int success = 0;
        String result = "";
        for (int d = 0; d < 2; d++)
        {
            // Do this twice to get both box offsets.
            for (int i = fo; i < 0xEE000; i++)
            {
                int err = 0;
                // Start at findoffset and see if it matches pattern
                if ((break1[i + 4] == break2[i + 4]) && (break1[i + 4 + 232] == break2[i + 4 + 232]))
                {
                    // Sanity Placeholders are the same
                    for (int j = 0; j < 4; j++)
                        if (break1[i + j] == break2[i + j])
                            err++;
                    if (err < 4)
                    {
                        // Keystream ^ PID doesn't match entirely. Keep checking.
                        for (int j = 8; j < 232; j++)
                            if (break1[i + j] == break2[i + j])
                                err++;
                        if (err < 20)
                        {
                            // Tolerable amount of difference between offsets. We have a result.
                            offset[d] = i;
                            break;
                        }
                    }
                }
            }
            fo = offset[d] + 232 * 30;  // Fast forward out of this box to find the next.
        }

        // Now that we have our two box offsets...
        // Check to see if we actually have them.
        if ((offset[0] == 0) || (offset[1] == 0))
        {
            // We have a problem. Don't continue.
            result = "Unable to Find Box.\n";
        }
        else
        {
            // Let's go deeper. We have the two box offsets.
            // Chunk up the base streams.
            byte[][] estream1 = new byte[30][232];
            byte[][] estream2 = new byte[30][232];
            // Stuff 'em.
            for (int i = 0; i < 30; i++)    // Times we're iterating
            {
                for (int j = 0; j < 232; j++)   // Stuff the Data
                {
                    estream1[i][j] = break1[offset[0] + 232 * i + j];
                    estream2[i][j] = break2[offset[1] + 232 * i + j];
                }
            }

            // Okay, now that we have the encrypted streams, formulate our EKX.
            String nick = eggnames[1];
            // Stuff in the nickname to our blank EKX.
            byte[] nicknamebytes = new byte[20];
            try
            {
                nicknamebytes = nick.getBytes("UTF-16LE");
            } catch (UnsupportedEncodingException e) { JOptionPane.showMessageDialog(this, "Error occurred during nickname stuffing.\n\n" + e, "Error", JOptionPane.ERROR_MESSAGE); }
            nicknamebytes = Arrays.copyOf(nicknamebytes, 24);
            System.arraycopy(nicknamebytes, 0, empty, 0x40, nicknamebytes.length);

            // Encrypt the Empty PKX to EKX.
            System.arraycopy(empty, 0, emptyekx, 0, 232);
            emptyekx = encryptArray(emptyekx);
            // Not gonna bother with the checksum, as this empty file is temporary.

            // Sweet. Now we just have to find the E0-E3 values. Let's get our polluted streams from each.
            // Save file 1 has empty box 1. Save file 2 has empty box 2.
            byte[][] pstream1 = new byte[30][232]; // Polluted Keystream 1
            byte[][] pstream2 = new byte[30][232]; // Polluted Keystream 2
            for (int i = 0; i < 30; i++)    // Times we're iterating
            {
                for (int j = 0; j < 232; j++)   // Stuff the Data
                {
                    pstream1[i][j] = (byte)(estream1[i][j]&0xff ^ emptyekx[j]&0xff);
                    pstream2[i][j] = (byte)(estream2[i][j]&0xff ^ emptyekx[j]&0xff);
                }
            }

            // Cool. So we have a fairly decent keystream to roll with. We now need to find what the E0-E3 region is.
            // 0x00000000 Encryption Constant has the D block last. 
            // We need to make sure our Supplied Encryption Constant Pokemon have the D block somewhere else (Pref in 1 or 3).

            // First, let's get out our polluted EKX's.
            byte[][] polekx = new byte[6][232];
            for (int i = 0; i < 6; i++)
                for (int j = 0; j < 232; j++) // Save file 1 has them in the second box. XOR them out with the Box2 Polluted Stream
                    polekx[i][j] = (byte)(break1[offset[1] + 232 * i + j]&0xff ^ pstream2[i][j]&0xff);
            int[] encryptionconstants = new int[6]; // Array for all 6 Encryption Constants. 
            int valid = 0;
            for (int i = 0; i < 6; i++)
            {
                encryptionconstants[i] = (int)polekx[i][0]&0xff;
                encryptionconstants[i] += ((int)polekx[i][1]&0xff) * 0x100;
                encryptionconstants[i] += ((int)polekx[i][2]&0xff) * 0x10000;
                encryptionconstants[i] += ((int)polekx[i][3]&0xff) * 0x1000000;
                // EC Obtained. Check to see if Block D is not last.
                if (getDloc(encryptionconstants[i]) != 3)
                {
                    valid++;
                    // Find the Origin/Region data.
                    byte[] encryptedekx;
                    byte[] decryptedpkx;
                    encryptedekx = Arrays.copyOf(polekx[i], 232);
                    decryptedpkx = decryptArray(encryptedekx);

                    // finalize data
                    // Okay, now that we have the encrypted streams, formulate our EKX.
                    nick = eggnames[decryptedpkx[0xE3] - 1];
                    // Stuff in the nickname to our blank EKX.
                    try
                    {
                        nicknamebytes = nick.getBytes("UTF-16LE");
                    } catch (UnsupportedEncodingException e) { JOptionPane.showMessageDialog(this, "Error occurred during nickname stuffing.\n\n" + e, "Error", JOptionPane.ERROR_MESSAGE); }
                    nicknamebytes = Arrays.copyOf(nicknamebytes, 24);
                    System.arraycopy(nicknamebytes, 0, empty, 0x40, nicknamebytes.length);

                    // Dump it into our Blank EKX. We have won!
                    empty[0xE0] = decryptedpkx[0xE0];
                    empty[0xE1] = decryptedpkx[0xE1];
                    empty[0xE2] = decryptedpkx[0xE2];
                    empty[0xE3] = decryptedpkx[0xE3];
                    break;
                }
            }

            if (valid == 0) // We didn't get any valid EC's where D was not in last. Tell the user to try again with different specimens.
                result = "The 6 supplied Pokemon are not suitable. \nRip new saves with 6 different ones that originated from your save file.\n";
            else
            {
                // We can continue to get our actual keystream.
                // Let's calculate the actual checksum of our empty pkx.
                int chk = 0;
                for (int i = 8; i < 232; i += 2) // Loop through the entire PKX
                    chk += BitConverter.ToUInt16(empty, i);

                // Apply New Checksum
                System.arraycopy(BitConverter.GetBytes(chk), 0, empty, 06, 2);

                // Okay. So we're now fixed with the proper blank PKX. Encrypt it!
                System.arraycopy(empty, 0, emptyekx, 0, 232);
                emptyekx = encryptArray(emptyekx);
                emptyekx = Arrays.copyOf(emptyekx, 232); // ensure it's 232 bytes.

                // Empty EKX obtained. Time to set up our key file.
                savkey = new byte[0xB4AD4];
                // Copy over 0x10-0x1F (Save Encryption Unused Data so we can track data).
                System.arraycopy(break1, 0x10, savkey, 0, 0x10);
                // Include empty data
                savkey[0x10] = empty[0xE0]; savkey[0x11] = empty[0xE1]; savkey[0x12] = empty[0xE2]; savkey[0x13] = empty[0xE3];
                // Copy over the scan offsets.
                System.arraycopy(BitConverter.GetBytes(offset[0]), 0, savkey, 0x1C, 4);
                for (int i = 0; i < 30; i++)    // Times we're iterating
                {
                    for (int j = 0; j < 232; j++)   // Stuff the Data temporarily...
                    {
                        savkey[0x100 + i * 232 + j] = (byte)(estream1[i][j]&0xff ^ emptyekx[j]&0xff);
                        savkey[0x100 + (30 * 232) + i * 232 + j] = (byte)(estream2[i][j]&0xff ^ emptyekx[j]&0xff);
                    }
                }

                // Let's extract some of the information now for when we set the Keystream filename.
                byte[] data1 = new byte[232];
                byte[] data2 = new byte[232];
                for (int i = 0; i < 232; i++)
                {
                    data1[i] = (byte)(savkey[0x100 + i]&0xff ^ break1[offset[0] + i]&0xff);
                    data2[i] = (byte)(savkey[0x100 + i]&0xff ^ break2[offset[0] + i]&0xff);
                }
                byte[] data1a = new byte[232]; byte[] data2a = new byte[232];
                System.arraycopy(data1, 0, data1a, 0, 232); System.arraycopy(data2, 0, data2a, 0, 232);
                byte[] pkx1 = decryptArray(data1);
                byte[] pkx2 = decryptArray(data2);
                int chk1 = 0;
                int chk2 = 0;
                for (int i = 8; i < 232; i += 2)
                {
                    chk1 += BitConverter.ToUInt16(pkx1, i);
                    chk2 += BitConverter.ToUInt16(pkx2, i);
                }
                if (verifyCHK(pkx1) && (BitConverter.ToUInt16(pkx1, 8) != 0))
                {
                    // Save 1 has the box1 data
                    pkx = pkx1;
                    success = 1;
                }
                else if (verifyCHK(pkx2) && (BitConverter.ToUInt16(pkx2, 8) != 0))
                {
                    // Save 2 has the box1 data
                    pkx = pkx2;
                    success = 1;
                }
                else
                {
                    // Data isn't decrypting right...
                    for (int i = 0; i < 232; i++)
                    {
                        data1a[i] ^= empty[i];
                        data2a[i] ^= empty[i];
                    }
                    pkx1 = decryptArray(data1a); pkx2 = decryptArray(data2a);
                    if (verifyCHK(pkx1) && (BitConverter.ToUInt16(pkx1, 8) != 0))
                    {
                        // Save 1 has the box1 data
                        pkx = pkx1;
                        success = 1;
                    }
                    else if (verifyCHK(pkx2) && (BitConverter.ToUInt16(pkx2, 8) != 0))
                    {
                        // Save 2 has the box1 data
                        pkx = pkx2;
                        success = 1;
                    }
                    else
                    {
                        // Sigh...
                    }
                }
            }
        }
        if (success == 1)
        {
            byte[] diff1 = new byte[31*30*232];
            byte[] diff2 = new byte[31*30*232];
            for(int i = 0; i < 31*30*232; ++i)
            {
                diff1[i] = (byte)(break1[offset[0] + i]&0xff ^ break1[offset[0] + i - 0x7F000]&0xff);
            }
            for(int i = 0; i < 31*30*232; ++i)
            {
                diff2[i] = (byte)(break2[offset[0] + i]&0xff ^ break2[offset[0] + i - 0x7F000]&0xff);
            }
            if (Arrays.equals(diff1, diff2))
            {
                boolean break3is1 = true;
                for(int i = (int)offset[0]; i<offset[0] + 31*30*232; ++i)
                {
                    if(!(break2[i] == break3[i]))
                    {
                        break3is1 = false;
                        break;
                    }
                }
                if (break3is1) save1Save = break3;
                slotsKey = diff1;
            }
            else success = 0;
        }
        if (success == 1)
        {
            // Markup the save to know that boxes 1 & 2 are dumpable.
            savkey[0x20] = 3; // 00000011 (boxes 1 & 2)

            // Clear the keystream file...
            for (int i = 0; i < 31; i++)
            {
                System.arraycopy(zerobox, 0, savkey, 0x00100 + i * (232 * 30), 232 * 30);
                System.arraycopy(zerobox, 0, savkey, 0x40000 + i * (232 * 30), 232 * 30);
            }

            // Copy the key for the slot selector
            System.arraycopy(save1Save, 0x168, savkey, 0x80000, 4);

            // Copy the key for the other save slot
            System.arraycopy(slotsKey, 0, savkey, 0x80004, 232*30*31);

            // Since we don't know if the user put them in in the wrong order, let's just markup our keystream with data.
            byte[] data1 = new byte[232];
            byte[] data2 = new byte[232];
            for (int i = 0; i < 31; i++)
            {
                for (int j = 0; j < 30; j++)
                {
                    System.arraycopy(break1, offset[0] + i * (232 * 30) + j * 232, data1, 0, 232);
                    System.arraycopy(break2, offset[0] + i * (232 * 30) + j * 232, data2, 0, 232);
                    if (Arrays.equals(data1, data2))
                    {
                        // Just copy data1 into the key file.
                        System.arraycopy(data1, 0, savkey, 0x00100 + i * (232 * 30) + j * 232, 232);
                    }
                    else
                    {
                        // Copy both datas into their keystream spots.
                        System.arraycopy(data1, 0, savkey, 0x00100 + i * (232 * 30) + j * 232, 232);
                        System.arraycopy(data2, 0, savkey, 0x40000 + i * (232 * 30) + j * 232, 232);
                    }
                }
            }

            // Save file diff is done, now we're essentially done. Save the keystream.
            if (JOptionPane.showConfirmDialog(this, "Keystreams were successfully bruteforced!\n\nClick OK to save your keystream.", "Prompt", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION)
            {
                // From our PKX data, fetch some details to name our key file...
                String ot = "";
                try { ot = TrimFromZero(new String(pkx, 0xB0, 24, "UTF-16LE")); }
                catch (UnsupportedEncodingException e) { JOptionPane.showMessageDialog(this, "Error retrieving OT name.\n\n" + e, "Error", JOptionPane.ERROR_MESSAGE); }
                int tid = BitConverter.ToUInt16(pkx, 0xC);
                int sid = BitConverter.ToUInt16(pkx, 0xE);
                int tsv = ((tid ^ sid) >> 4);
                Path newPath = Paths.get(path_exe, "data", CleanFileName(String.format("SAV Key - %s - (%05d.%05d) - TSV %04d.bin", ot, tid, sid, tsv)));
                boolean doit = true;
                if (Files.exists(newPath))
                {
                    if (JOptionPane.showConfirmDialog(this, "Keystream already exists!\n\nOverwrite?", "Prompt", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
                    {
                        try { Files.delete(newPath); }
                        catch (IOException e) { JOptionPane.showMessageDialog(this, "Error deleting keystream file.\n\n" + e, "Error", JOptionPane.ERROR_MESSAGE);}
                    }
                    else
                    {
                        doit = false;
                        JOptionPane.showMessageDialog(this, "Chose not to save keystream.", "Alert", JOptionPane.WARNING_MESSAGE);
                    }
                }
                if (doit)
                {
                    try { Files.write(newPath, savkey); }
                    catch (IOException e) { JOptionPane.showMessageDialog(this, "Error writing keystream file.\n\n" + e, "Error", JOptionPane.ERROR_MESSAGE);}
                    JOptionPane.showMessageDialog(this, "Keystream saved to file:\n\n" + newPath.toString());
                }
            }
            else
            {
                JOptionPane.showMessageDialog(this, "Chose not to save keystream.", "Alert", JOptionPane.WARNING_MESSAGE);
            }
        }
        else // Failed
            JOptionPane.showMessageDialog(this, "Keystreams were NOT bruteforced!\n\nStart over and try again :(", "Failure", JOptionPane.ERROR_MESSAGE);
    }
    
    // Utility
    private byte[] xortwos(byte[] arr1, byte[] arr2)
    {
        if (arr1.length != arr2.length) return null;
        byte[] arr3 = new byte[arr1.length];
        for (int i = 0; i < arr1.length; i++)
            arr3[i] = (byte)(arr1[i]&0xff ^ arr2[i]&0xff);
        return arr3;
    }

    private static String TrimFromZero(String input)
    {
        int index = input.indexOf('\0');
        if (index < 0)
            return input;

        return input.substring(0, index);
    }

    private static String CleanFileName(String fileName)
    {
        return fileName;
        // return Path.GetInvalidFileNameChars().Aggregate(fileName, (current, c) => current.Replace(c.ToString(), string.Empty));
    }

    // Get the header string
    private String getHeaderString(String format, boolean isSAV)
    {
        // Only output Row,Col for CSV output for SAVs
        String slotString = (isSAV && (CB_ExportStyle.getSelectedIndex() == 6 || CB_ExportStyle.getSelectedIndex() == 7)) ? "Row,Col" : "Slot";

        return String.format(format, "Box", slotString, "Species", "Gender", "Nature", "Ability", "HP", "ATK", "DEF", "SPA", "SPD", "SPE", "HiddenPower", "ESV", "TSV", "Nickname", "OT", "Ball", "TID", "SID", "HP EV", "ATK EV", "DEF EV", "SPA EV", "SPD EV", "SPE EV", "Move 1", "Move 2", "Move 3", "Move 4", "Relearn 1", "Relearn 2", "Relearn 3", "Relearn 4", "Shiny", "Egg", "Level", "Region", "Country", "Held Item", "Language", "Game", "Slot", "PID", "Mark", "Dex Number", "Form", "1", "2", "3", "4", "5", "6", "IVs", "IV Sum", "EV Sum", "Egg Received", "Met/Hatched", "Exp", "Count", "Infected", "Cured", "OTG", "Met Level", "Friendship", "Affection", "Steps to Hatch", "Ball", "HA");
    }
    
    // SD Detection
    private void changedetectgame()
    {
        game = CB_Game.getSelectedIndex();
    }

    private void detectMostRecent()
    {
        // Fetch the selected save file and video
        if (game == 0)
        {
            // X
            savpath = Paths.get(path_3DS, "title", "00040000", "00055d00", "data").toString(); 
            vidpath = Paths.get(path_3DS, "extdata", "00000000", "0000055d", "00000000").toString(); 
        }
        else if (game == 1)
        {
            // Y
            savpath = Paths.get(path_3DS, "title", "00040000", "00055e00", "data").toString(); 
            vidpath = Paths.get(path_3DS, "extdata", "00000000", "0000055e", "00000000").toString(); 
        }
        else if (game == 2) 
        {
            // OR
            savpath = Paths.get(path_3DS, "title", "00040000", "0011c400", "data").toString();
            vidpath = Paths.get(path_3DS, "extdata", "00000000", "000011c4", "00000000").toString();
        }
        else if (game == 3)
        {
            // AS
            savpath = Paths.get(path_3DS, "title", "00040000", "0011c500", "data").toString();
            vidpath = Paths.get(path_3DS, "extdata", "00000000", "000011c5", "00000000").toString();
        }

        // Go ahead and open the save automatically if found
        if (Files.isDirectory(Paths.get(savpath)))
        {
            if (Files.exists(Paths.get(savpath, "00000001.sav")))
                openSAV(Paths.get(savpath, "00000001.sav").toFile());
        }

        // Fetch the latest video
        if (Files.isDirectory(Paths.get(vidpath)))
        {
            try (DirectoryStream files = Files.newDirectoryStream(Paths.get(vidpath));)
            {
                long newest = 0;
                Path BV = Paths.get(vidpath);
                while (files.iterator().hasNext())
                {
                    Path thisOne = (Path)files.iterator().next();
                    long thisTime = Files.getLastModifiedTime(thisOne).toMillis();
                    if (thisTime > newest)
                    {
                        BV = thisOne;
                        newest = thisTime;
                    }
                }
                if (BV.toFile().length() == 28256)
                openVID(BV.toFile());
            }
            catch (IOException e) { }
        }
    }

    private void find3DS()
    {
        /*
        // start by checking if the 3DS file path exists or not.
        string[] DriveList = Environment.GetLogicalDrives();
        for (int i = 1; i < DriveList.Length; i++)
        {
            path_3DS = DriveList[i] + "Nintendo 3DS";
            if (Directory.Exists(path_3DS))
                break;
            path_3DS = null;
        }
        if (path_3DS == null) // No 3DS SD Card Detected
            return;
        else
        {
            // 3DS data found in SD card reader. Let's get the title folder location!
            string[] folders = Directory.GetDirectories(path_3DS, "*", System.IO.SearchOption.AllDirectories);

            // Loop through all the folders in the Nintendo 3DS folder to see if any of them contain 'title'.
            for (int i = 0; i < folders.Length; i++)
            {
                DirectoryInfo di = new DirectoryInfo(folders[i]);
                if (di.Name == "title" || di.Name == "extdata")
                {
                    path_3DS = di.Parent.FullName.ToString();
                    myTimer.Stop();
                    detectMostRecent();
                    return;
                }
            }
        }
        */
    }

    // UI Prompted Updates
    private void changeboxsetting(java.awt.event.ActionEvent evt)
    {
        boolean bool = !(CB_BoxStart.getSelectedItem().toString().equals("All"));
        CB_BoxEnd.setVisible(bool);
        CB_BoxEnd.setEnabled(bool);
        L_BoxThru.setVisible(bool);
        if (bool)
        {
            int start = Integer.parseInt(CB_BoxStart.getSelectedItem().toString());
            int oldValue;
            try {oldValue = Integer.parseInt(CB_BoxEnd.getSelectedItem().toString()); } catch (NumberFormatException e) {oldValue = 1;}
            CB_BoxEnd.removeAllItems();
            for (int i = start; i < 32; i++)
                CB_BoxEnd.addItem(Integer.toString(i));
            CB_BoxEnd.setSelectedIndex(start >= oldValue ? 0 : oldValue-start);
        }
    }

    private void B_ShowOptions_Click(java.awt.event.ActionEvent evt)
    {
        /*
        Help.GetHelp.Show();
        */
    }

    private void changeExportStyle(java.awt.event.ActionEvent evt)
    {
        /*
            Default
            Reddit
            TSV
            Custom 1
            Custom 2
            Custom 3
            CSV default
            CSV custom
            To .PK6 File 
         */
        if (CB_ExportStyle.getSelectedIndex() == 0) // Default
        {
            CHK_BoldIVs.setEnabled(false);
            CHK_ColorBox.setEnabled(false);
            CB_BoxColor.setEnabled(false);
            CHK_R_Table.setEnabled(false);
            CHK_NameQuotes.setEnabled(false);
            B_ResetCSV.setEnabled(false);
            RTB_OPTIONS.setEditable(false); RTB_OPTIONS.setText("{0} - {1} - {2} ({3}) - {4} - {5} - {6}.{7}.{8}.{9}.{10}.{11} - {12} - {13}");
        }
        else if (CB_ExportStyle.getSelectedIndex() == 1) // Reddit
        {
            CHK_BoldIVs.setEnabled(true);
            CHK_ColorBox.setEnabled(true);
            CB_BoxColor.setEnabled(true);
            CHK_R_Table.setEnabled(false);
            CHK_R_Table.setSelected(true);
            CHK_NameQuotes.setEnabled(false);
            B_ResetCSV.setEnabled(false);
            RTB_OPTIONS.setEditable(false); RTB_OPTIONS.setText("{0} | {1} | {2} ({3}) | {4} | {5} | {6}.{7}.{8}.{9}.{10}.{11} | {12} | {13} |");
        }
        else if (CB_ExportStyle.getSelectedIndex() == 2) // TSV
        {
            CHK_BoldIVs.setEnabled(true);
            CHK_ColorBox.setEnabled(true);
            CB_BoxColor.setEnabled(true);
            CHK_R_Table.setEnabled(false);
            CHK_R_Table.setSelected(true);
            CHK_NameQuotes.setEnabled(false);
            B_ResetCSV.setEnabled(false);
            RTB_OPTIONS.setEditable(false); RTB_OPTIONS.setText("{0} | {1} | {16} | {18} | {14} |");
        }
        else if (CB_ExportStyle.getSelectedIndex() == 3) // Custom 1
        {
            CHK_NameQuotes.setEnabled(false);
            B_ResetCSV.setEnabled(false);
            CHK_R_Table.setEnabled(true); CHK_R_Table.setSelected(custom1b);
            CHK_BoldIVs.setEnabled(true);
            CHK_ColorBox.setEnabled(true);
            CB_BoxColor.setEnabled(true);
            RTB_OPTIONS.setEditable(true);
            RTB_OPTIONS.setText(custom1);
        }
        else if (CB_ExportStyle.getSelectedIndex() == 4) // Custom 2
        {
            CHK_NameQuotes.setEnabled(false);
            B_ResetCSV.setEnabled(false);
            CHK_R_Table.setEnabled(true); CHK_R_Table.setSelected(custom2b);
            CHK_BoldIVs.setEnabled(true);
            CHK_ColorBox.setEnabled(true);
            CB_BoxColor.setEnabled(true);
            RTB_OPTIONS.setEditable(true);
            RTB_OPTIONS.setText(custom2);
        }
        else if (CB_ExportStyle.getSelectedIndex() == 5) // Custom 3
        {
            CHK_NameQuotes.setEnabled(false);
            B_ResetCSV.setEnabled(false);
            CHK_R_Table.setEnabled(true); CHK_R_Table.setSelected(custom3b);
            CHK_BoldIVs.setEnabled(true);
            CHK_ColorBox.setEnabled(true);
            CB_BoxColor.setEnabled(true);
            RTB_OPTIONS.setEditable(true);
            RTB_OPTIONS.setText(custom3);
        }
        else if (CB_ExportStyle.getSelectedIndex() == 6) // CSV
        {
            CHK_BoldIVs.setEnabled(false);
            CHK_ColorBox.setEnabled(false);
            CB_BoxColor.setEnabled(false);
            CHK_R_Table.setEnabled(false);
            B_ResetCSV.setEnabled(false);
            CHK_NameQuotes.setEnabled(true);
            RTB_OPTIONS.setEditable(false); RTB_OPTIONS.setText("{0},{1},{2},{3},{4},{5},{6},{7},{8},{9},{10},{11},{12},{13},{14},{15},{16},{17},{18},{19},{20},{21},{22},{23},{24},{25},{26},{27},{28},{29},{30},{31},{32},{33},{34},{35}");
        }
        else if (CB_ExportStyle.getSelectedIndex() == 7) // CSV custom
        {
            CHK_BoldIVs.setEnabled(false);
            CHK_ColorBox.setEnabled(false);
            CB_BoxColor.setEnabled(false);
            CHK_R_Table.setEnabled(false);
            CHK_NameQuotes.setEnabled(true);
            RTB_OPTIONS.setEditable(true);
            B_ResetCSV.setEnabled(true);
            // If nothing is saved, fill with all columns by default
            RTB_OPTIONS.setText((customcsv.equals("")) ? defaultCSVcustom : customcsv);
        }
        else if (CB_ExportStyle.getSelectedIndex() == 8) // PK6
        {
            CHK_BoldIVs.setEnabled(false);
            CHK_ColorBox.setEnabled(false);
            CB_BoxColor.setEnabled(false);
            CHK_R_Table.setEnabled(false);
            CHK_NameQuotes.setEnabled(false);
            B_ResetCSV.setEnabled(false);
            RTB_OPTIONS.setEditable(false); RTB_OPTIONS.setText("Files will be saved in .PK6 format, and the default method will display.");
        }

        // Update the format preview on format change
        updatePreview();
    }

    private void changeFormatText()
    {
        if (CB_ExportStyle.getSelectedIndex() == 3) // Custom 1
            custom1 = RTB_OPTIONS.getText();
        else if (CB_ExportStyle.getSelectedIndex() == 4) // Custom 2
            custom2 = RTB_OPTIONS.getText();
        else if (CB_ExportStyle.getSelectedIndex() == 5) // Custom 3
            custom3 = RTB_OPTIONS.getText();
        else if (CB_ExportStyle.getSelectedIndex() == 7) // CSV custom
            customcsv = RTB_OPTIONS.getText();

        // Update format preview whenever it's changed
        updatePreview();
    }

    private void changeTableStatus(java.awt.event.ItemEvent evt)
    {
        if (CB_ExportStyle.getSelectedIndex() == 3) // Custom 1
            custom1b = CHK_R_Table.isSelected();
        else if (CB_ExportStyle.getSelectedIndex() == 4) // Custom 2
            custom2b = CHK_R_Table.isSelected();
        else if (CB_ExportStyle.getSelectedIndex() == 5) // Custom 3
            custom3b = CHK_R_Table.isSelected();
    }

    private void changeReadOnly()
    {
        /*
        RichTextBox rtb = sender as RichTextBox;
        if (rtb.ReadOnly) rtb.BackColor = Color.FromKnownColor(KnownColor.Control);
        else rtb.BackColor = Color.FromKnownColor(KnownColor.White);
        */
    }

    // Update Text Format Preview
    private void updatePreview()
    {
        // Catch a format exception to let the user finish typing formats
        try { RTB_Preview.setText(getHeaderString(RTB_OPTIONS.getText(), true)); }
        catch (Exception e) { /* Do nothing */ }
    }

    // Translation
    private void changeLanguage(java.awt.event.ActionEvent evt)
    {
        InitializeStrings();
    }

    private String[] getStringList(String f, String l)
    {
        String directory = (l.equals("all")) ? "" : l + "/";
        try (InputStream in = this.getClass().getResourceAsStream("/keysav3/resources/text/" + directory + "text_" + f + "_" + l + ".txt");)
        {
            BufferedReader r = new BufferedReader(new InputStreamReader(in));
            String line;
            ArrayList<String> stringdata = new ArrayList();
            while ((line = r.readLine()) != null)
                stringdata.add(line.trim());
            in.close();
            return stringdata.toArray(new String[stringdata.size()]);
        } catch (IOException e) { return null; }
    }

    private void InitializeStrings()
    {
        int curAbility = -1;
        try
        {
            if (!CB_Abilities.getSelectedItem().toString().equals(""))
            {
                for (int i = 0; !abilitylist[i].equals(CB_Abilities.getSelectedItem().toString()); ++i)
                    curAbility = i;
            }
        }
        catch (Exception e)
        {
            curAbility = -1;
        }
        String[] lang_val = { "en", "ja", "fr", "it", "de", "es", "ko" };
        String l = lang_val[CB_MainLanguage.getSelectedIndex()];
        natures = getStringList("Natures", l);
        types = getStringList("Types", l);
        abilitylist = getStringList("Abilities", l);
        movelist = getStringList("Moves", l);
        itemlist = getStringList("Items", l);
        specieslist = getStringList("Species", l);
        formlist = getStringList("Forms", l);
        countryList = getStringList("Countries", l);
        regionList = getStringList("Regions", l);
        gameList = getStringList("Games", l);
        expGrowth = getStringList("expGrowth", "all");
        int[] ballindex = {
                              0,1,2,3,4,5,6,7,8,9,0xA,0xB,0xC,0xD,0xE,0xF,0x10,
                              0x1EC,0x1ED,0x1EE,0x1EF,0x1F0,0x1F1,0x1F2,0x1F3,
                              0x240 
                          };
        balls = new String[ballindex.length];
        for (int i = 0; i < ballindex.length; i++)
            balls[i] = itemlist[ballindex[i]];

        // vivillon pattern list
        vivlist = new String[20];
        vivlist[0] = formlist[666];
        for (int i = 1; i < 20; i++)
            vivlist[i] = formlist[835+i];

        // Populate natures in filters
        CCB_Natures.getModel().clear();
        CCB_Natures.getModel().addElement(0, "All");
        for (int i = 0; i < natures.length; i++)
            CCB_Natures.getModel().addElement(i + 1, natures[i]);
        // CCB_Natures.DisplayMember = "Name";
        CCB_Natures.getModel().setCheck("All");

        // Populate HP types in filters
        CCB_HPType.getModel().clear();
        CCB_HPType.getModel().addElement(0, "Any");
        for (int i = 1; i < types.length-1; i++)
            CCB_HPType.getModel().addElement(i + 1, types[i]);
        // CCB_HPType.DisplayMember = "Name";
        CCB_HPType.getModel().setCheck("All");

        // Populate ability list
        String[] sortedAbilities = abilitylist.clone();
        Arrays.sort(sortedAbilities);
        CB_Abilities.removeAllItems();
        for (String item : sortedAbilities)
            CB_Abilities.addItem(item);
        if (curAbility != -1) CB_Abilities.setSelectedItem(abilitylist[curAbility]);
    }

    // Based on method in PkHex
    private int getLevel(int species, long exp)
    {
        if (exp == 0) return 1;
        int growth = Integer.parseInt(expGrowth[species]);

        // Iterate upwards to find the level above our current level
        int level = 0; // Initial level, immediately incremented before loop
        while (expTable[++level][growth] <= exp)
        {
            if (level == 100)
                return level;
            // After we find the level above ours, we're done
        }
        return --level;
    }
    
    // Structs
    public static class Structures
    {
        public static class PKX
        {
            public long
                EC, exp, PID;
            
            public int
                HP_EV, ATK_EV, DEF_EV, SPA_EV, SPD_EV, SPE_EV,
                HP_IV, ATK_IV, DEF_IV, SPE_IV, SPA_IV, SPD_IV,
                cnt_cool, cnt_beauty, cnt_cute, cnt_smart, cnt_tough, cnt_sheen,
                markings, hptype;

            public String
                nicknamestr, notOT, ot, genderstring;

            public int
                IV32, move1, move2, move3, move4, eggmove1, eggmove2, eggmove3, eggmove4,
                ability, abilitynum, nature, feflag, genderflag, altforms, PKRS_Strain, PKRS_Duration,
                metlevel, otgender, species, helditem, TID, SID, chk, eggloc, metloc;

            public boolean
                isegg, isnick, isshiny;

            public short
                TSV, ESV,
                move1_pp, move2_pp, move3_pp, move4_pp,
                move1_ppu, move2_ppu, move3_ppu, move4_ppu,
                OTfriendship, OTaffection,
                egg_year, egg_month, egg_day,
                met_year, met_month, met_day,
                ball, encountertype,
                gamevers, countryID, regionID, dsregID, otlang;

            public PKX(byte[] pkx)
            {
                nicknamestr = "";
                notOT = "";
                ot = "";
                EC = BitConverter.ToUInt32(pkx, 0);
                chk = BitConverter.ToUInt16(pkx, 6);
                species = BitConverter.ToUInt16(pkx, 0x08);
                helditem = BitConverter.ToUInt16(pkx, 0x0A);
                TID = BitConverter.ToUInt16(pkx, 0x0C);
                SID = BitConverter.ToUInt16(pkx, 0x0E);
                exp = BitConverter.ToUInt32(pkx, 0x10);
                ability = pkx[0x14];
                abilitynum = pkx[0x15];
                // 0x16, 0x17 - unknown
                PID = BitConverter.ToUInt32(pkx, 0x18);
                nature = pkx[0x1C];
                feflag = pkx[0x1D] % 2;
                genderflag = (pkx[0x1D] >> 1) & 0x3;
                altforms = (pkx[0x1D] >> 3);
                HP_EV = pkx[0x1E];
                ATK_EV = pkx[0x1F];
                DEF_EV = pkx[0x20];
                SPA_EV = pkx[0x22];
                SPD_EV = pkx[0x23];
                SPE_EV = pkx[0x21];
                cnt_cool = pkx[0x24];
                cnt_beauty = pkx[0x25];
                cnt_cute = pkx[0x26];
                cnt_smart = pkx[0x27];
                cnt_tough = pkx[0x28];
                cnt_sheen = pkx[0x29];
                markings = pkx[0x2A];
                PKRS_Strain = pkx[0x2B] >> 4;
                PKRS_Duration = pkx[0x2B] % 0x10;

                // Block B
                try
                {
                nicknamestr = TrimFromZero(new String(pkx, 0x40, 24, "UTF-16LE"));
                notOT = TrimFromZero(new String(pkx, 0x78, 24, "UTF-16LE"));
                ot = TrimFromZero(new String(pkx, 0xB0, 24, "UTF-16LE"));
                }
                catch (UnsupportedEncodingException e) { nicknamestr = notOT = ot = "ERROR"; }
                // 0x58, 0x59 - unused
                move1 = BitConverter.ToUInt16(pkx, 0x5A);
                move2 = BitConverter.ToUInt16(pkx, 0x5C);
                move3 = BitConverter.ToUInt16(pkx, 0x5E);
                move4 = BitConverter.ToUInt16(pkx, 0x60);
                move1_pp = pkx[0x62];
                move2_pp = pkx[0x63];
                move3_pp = pkx[0x64];
                move4_pp = pkx[0x65];
                move1_ppu = pkx[0x66];
                move2_ppu = pkx[0x67];
                move3_ppu = pkx[0x68];
                move4_ppu = pkx[0x69];
                eggmove1 = BitConverter.ToUInt16(pkx, 0x6A);
                eggmove2 = BitConverter.ToUInt16(pkx, 0x6C);
                eggmove3 = BitConverter.ToUInt16(pkx, 0x6E);
                eggmove4 = BitConverter.ToUInt16(pkx, 0x70);

                // 0x72 - Super Training Flag - Passed with pkx to new form

                // 0x73 - unused/unknown
                IV32 = (int)BitConverter.ToUInt32(pkx, 0x74);
                HP_IV = IV32 & 0x1F;
                ATK_IV = (IV32 >> 5) & 0x1F;
                DEF_IV = (IV32 >> 10) & 0x1F;
                SPE_IV = (IV32 >> 15) & 0x1F;
                SPA_IV = (IV32 >> 20) & 0x1F;
                SPD_IV = (IV32 >> 25) & 0x1F;
                isegg = (((IV32 >> 30) & 1) != 0);
                isnick = ((IV32 >> 31) != 0);

                // Block C

                boolean notOTG = (pkx[0x92] != 0);
                // Memory Editor edits everything else with pkx in a new form

                // Block D
                // 0xC8, 0xC9 - unused
                OTfriendship = pkx[0xCA];
                OTaffection = pkx[0xCB]; // Handled by Memory Editor
                // 0xCC, 0xCD, 0xCE, 0xCF, 0xD0
                egg_year = pkx[0xD1];
                egg_month = pkx[0xD2];
                egg_day = pkx[0xD3];
                met_year = pkx[0xD4];
                met_month = pkx[0xD5];
                met_day = pkx[0xD6];
                // 0xD7 - unused
                eggloc = BitConverter.ToUInt16(pkx, 0xD8);
                metloc = BitConverter.ToUInt16(pkx, 0xDA);
                ball = pkx[0xDC];
                metlevel = pkx[0xDD] & 0x7F;
                otgender = (pkx[0xDD]) >> 7;
                encountertype = pkx[0xDE];
                gamevers = pkx[0xDF];
                countryID = pkx[0xE0];
                regionID = pkx[0xE1];
                dsregID = pkx[0xE2];
                otlang = pkx[0xE3];

                if (genderflag == 0)
                    genderstring = "♂";
                else if (genderflag == 1)
                    genderstring = "♀";
                else genderstring = "-";

                hptype = (15 * ((HP_IV & 1) + 2 * (ATK_IV & 1) + 4 * (DEF_IV & 1) + 8 * (SPE_IV & 1) + 16 * (SPA_IV & 1) + 32 * (SPD_IV & 1))) / 63 + 1;

                TSV = (short)((TID ^ SID) >> 4);
                ESV = (short)(((PID >> 16) ^ (PID & 0xFFFF)) >> 4);

                isshiny = (TSV == ESV);
            }
        }
    }

    // UI button actions
    private void B_BKP_SAV_Click(java.awt.event.ActionEvent evt)
    {
        /*
        TextBox tb = TB_SAV;
        FileInfo fi = new FileInfo(tb.Text);
        DateTime dt = fi.LastWriteTime;
        int year = dt.Year;
        int month = dt.Month;
        int day = dt.Day;
        int hour = dt.Hour;
        int minute = dt.Minute;
        int second = dt.Second;
        string bkpdate = year.ToString("0000") + month.ToString("00") + day.ToString("00") + hour.ToString("00") + minute.ToString("00") + second.ToString("00") + " ";
        string newpath = bakpath + Path.DirectorySeparatorChar + bkpdate + fi.Name;
        if (File.Exists(newpath))
        {
            DialogResult sdr = MessageBox.Show("File already exists!\n\nOverwrite?", "Prompt", MessageBoxButtons.YesNo);
            if (sdr == DialogResult.Yes)
                File.Delete(newpath);
            else 
                return;
        }
        File.Copy(tb.Text, newpath);
        MessageBox.Show("Copied to Backup Folder.\n\nFile named:\n" + newpath, "Alert");
        */
    }

    private void B_BKP_BV_Click()
    {
        /*
        TextBox tb = TB_BV;
        FileInfo fi = new FileInfo(tb.Text);
        DateTime dt = fi.LastWriteTime;
        int year = dt.Year;
        int month = dt.Month;
        int day = dt.Day;
        int hour = dt.Hour;
        int minute = dt.Minute;
        int second = dt.Second;
        string bkpdate = year.ToString("0000") + month.ToString("00") + day.ToString("00") + hour.ToString("00") + minute.ToString("00") + second.ToString("00") + " ";
        string newpath = bakpath + Path.DirectorySeparatorChar + bkpdate + fi.Name;
        if (File.Exists(newpath))
        {
            DialogResult sdr = MessageBox.Show("File already exists!\n\nOverwrite?", "Prompt", MessageBoxButtons.YesNo);
            if (sdr == DialogResult.Yes)
                File.Delete(newpath);
            else 
                return;
        }
        File.Copy(tb.Text, newpath);
        MessageBox.Show("Copied to Backup Folder.\n\nFile named:\n" + newpath, "Alert");
        */
    }

    private void B_BreakFolder_Click(java.awt.event.ActionEvent evt)
    {
        /*
        int i = 0;
        DialogResult sdr = MessageBox.Show("This will improve your keystream by scanning saves in the folder you selected.\n\nThis may take some time. Press OK to continue.", "Prompt", MessageBoxButtons.OKCancel);
        if (sdr == DialogResult.OK)
        {
            byte[] savefile = new byte[0x10009C];
            string savkeypath = "";
            binType = "sav";
            string[] files = Directory.GetFiles(TB_Folder.Text);
            FolderBar.Maximum = files.Length;
            FolderBar.Step = 1;
            foreach (string path in files)
            {
                i += openSAV_(path, ref savefile, ref savkeypath, false);
                FolderBar.PerformStep();
            }
        }
        MessageBox.Show("Processed " + i + " saves in folder:\n\n" + TB_Folder.Text, "Prompt");
        FolderBar.Value = 0;
        */
    }

    private void B_ResetCSV_Click(java.awt.event.ActionEvent evt)
    {
        /*
        DialogResult box = MessageBox.Show("This will erase your current CSV custom format and replace it with the default CSV custom format, which includes ALL columns.\n\nContinue?", "Warning", MessageBoxButtons.YesNo);
        if (box == DialogResult.Yes)
        {
            customcsv = defaultCSVcustom;
            RTB_OPTIONS.Text = defaultCSVcustom;
            updatePreview();
            return;
        }
        else return;
        */
    }

    private void toggleIVAll(java.awt.event.ItemEvent evt)
    {
        /*
        if(updateIVCheckboxes)
            switch ((new [] {CHK_IV_HP, CHK_IV_Atk, CHK_IV_Def, CHK_IV_SpAtk, CHK_IV_SpDef, CHK_IV_Spe}).Count(c => c.Checked))
            {
                case 0:
                    CHK_IVsAny.CheckState = CheckState.Unchecked;
                    break;
                case 6:
                    CHK_IVsAny.CheckState = CheckState.Checked;
                    break;
                default:
                    CHK_IVsAny.CheckState = CheckState.Indeterminate;
                    break;
            }
        */
    }

    private void toggleIVsAny(java.awt.event.ItemEvent evt)
    {
        /*
        updateIVCheckboxes = false;
        if (CHK_IVsAny.CheckState != CheckState.Indeterminate)
            foreach (var box in new [] {CHK_IV_HP, CHK_IV_Atk, CHK_IV_Def, CHK_IV_SpAtk, CHK_IV_SpDef, CHK_IV_Spe})
                box.Checked = CHK_IVsAny.Checked;
        updateIVCheckboxes = true;
        */
    }

    private void toggleTrickroom(java.awt.event.ItemEvent evt)
    {
        CHK_IV_Spe.setText(CHK_Trickroom.isSelected() ? "Spe (= 0)" : "Speed");
    }

    private void toggleSpecialAttacker(java.awt.event.ItemEvent evt)
    {
        CHK_IV_Atk.setText(CHK_Special_Attacker.isSelected() ? "Atk (= 0)" : "Attack");
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        RAD_Gender = new javax.swing.ButtonGroup();
        B_ShowOptions = new javax.swing.JButton();
        CB_Game = new javax.swing.JComboBox();
        label3 = new javax.swing.JLabel();
        tab_Main = new javax.swing.JTabbedPane();
        tab_BV = new javax.swing.JPanel();
        B_OpenVideo = new javax.swing.JButton();
        TB_BV = new javax.swing.JTextField();
        L_KeyBV = new javax.swing.JLabel();
        B_GoBV = new javax.swing.JButton();
        L_BVTeam = new javax.swing.JLabel();
        CB_Team = new javax.swing.JComboBox();
        B_BKP_BV = new javax.swing.JButton();
        RTB_VID_Scroll = new javax.swing.JScrollPane();
        RTB_VID = new javax.swing.JTextArea();
        tab_SAV = new javax.swing.JPanel();
        B_OpenSAV = new javax.swing.JButton();
        TB_SAV = new javax.swing.JTextField();
        L_KeySAV = new javax.swing.JLabel();
        B_GoSAV = new javax.swing.JButton();
        L_BoxSAV = new javax.swing.JLabel();
        CB_BoxStart = new javax.swing.JComboBox();
        B_BKP_SAV = new javax.swing.JButton();
        RTB_SAV_Scroll = new javax.swing.JScrollPane();
        RTB_SAV = new javax.swing.JTextArea();
        CB_BoxEnd = new javax.swing.JComboBox();
        L_BoxThru = new javax.swing.JLabel();
        GB_Filter = new javax.swing.JPanel();
        CHK_Egg = new javax.swing.JCheckBox();
        L_No_IVs = new javax.swing.JLabel();
        CB_No_IVs = new javax.swing.JComboBox();
        CHK_Trickroom = new javax.swing.JCheckBox();
        CHK_Special_Attacker = new javax.swing.JCheckBox();
        L_IVsMiss = new javax.swing.JLabel();
        CHK_IV_HP = new javax.swing.JCheckBox();
        CHK_IV_Atk = new javax.swing.JCheckBox();
        CHK_IV_Def = new javax.swing.JCheckBox();
        CHK_IV_SpAtk = new javax.swing.JCheckBox();
        CHK_IV_SpDef = new javax.swing.JCheckBox();
        CHK_IV_Spe = new javax.swing.JCheckBox();
        L_HP_Type = new javax.swing.JLabel();
        CHK_IVsAny = new javax.swing.JCheckBox();
        CHK_Is_Shiny = new javax.swing.JCheckBox();
        CHK_Hatches_Shiny_For = new javax.swing.JCheckBox();
        TB_SVs = new javax.swing.JTextField();
        CHK_Hatches_Shiny_For_Me = new javax.swing.JCheckBox();
        RAD_Male = new javax.swing.JRadioButton();
        RAD_Female = new javax.swing.JRadioButton();
        RAD_GenderAny = new javax.swing.JRadioButton();
        L_Nature = new javax.swing.JLabel();
        CHK_Has_HA = new javax.swing.JCheckBox();
        L_Ability = new javax.swing.JLabel();
        CB_Abilities = new javax.swing.JComboBox();
        CCB_HPType = new org.japura.gui.CheckComboBox();
        CCB_Natures = new org.japura.gui.CheckComboBox();
        CHK_Enable_Filtering = new javax.swing.JCheckBox();
        L_SAVStats = new javax.swing.JLabel();
        tab_Options = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        L_Language = new javax.swing.JLabel();
        L_ExportStyle = new javax.swing.JLabel();
        CB_MainLanguage = new javax.swing.JComboBox();
        CB_ExportStyle = new javax.swing.JComboBox();
        CHK_MarkFirst = new javax.swing.JCheckBox();
        CHK_HideFirst = new javax.swing.JCheckBox();
        CHK_Split = new javax.swing.JCheckBox();
        CHK_Header = new javax.swing.JCheckBox();
        CHK_Unicode = new javax.swing.JCheckBox();
        CHK_ShowESV = new javax.swing.JCheckBox();
        jPanel2 = new javax.swing.JPanel();
        CHK_R_Table = new javax.swing.JCheckBox();
        CHK_BoldIVs = new javax.swing.JCheckBox();
        CHK_ColorBox = new javax.swing.JCheckBox();
        CB_BoxColor = new javax.swing.JComboBox();
        jPanel3 = new javax.swing.JPanel();
        CHK_NameQuotes = new javax.swing.JCheckBox();
        B_ResetCSV = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        RTB_OPTIONS = new javax.swing.JTextArea();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        RTB_Preview = new javax.swing.JTextArea();
        tab_Breaker = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        L_instrSAV = new javax.swing.JLabel();
        B_Break = new javax.swing.JButton();
        B_File1 = new javax.swing.JButton();
        TB_File1 = new javax.swing.JTextField();
        B_File2 = new javax.swing.JButton();
        TB_File2 = new javax.swing.JTextField();
        B_File3 = new javax.swing.JButton();
        TB_File3 = new javax.swing.JTextField();
        jPanel7 = new javax.swing.JPanel();
        L_instrFolder = new javax.swing.JLabel();
        B_BreakFolder = new javax.swing.JButton();
        B_Folder = new javax.swing.JButton();
        TB_Folder = new javax.swing.JTextField();
        FolderBar = new javax.swing.JProgressBar();
        jPanel8 = new javax.swing.JPanel();
        L_instrBV = new javax.swing.JLabel();
        B_BreakBV = new javax.swing.JButton();
        BVFile1 = new javax.swing.JButton();
        TB_FileBV1 = new javax.swing.JTextField();
        BVFile2 = new javax.swing.JButton();
        TB_FileBV2 = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("KeySAV3");
        setMaximumSize(new java.awt.Dimension(700, 750));
        setMinimumSize(new java.awt.Dimension(390, 588));
        setPreferredSize(new java.awt.Dimension(390, 588));

        B_ShowOptions.setText("Help");
        B_ShowOptions.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B_ShowOptions_Click(evt);
            }
        });

        CB_Game.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "X", "Y", "OR", "AS" }));

        label3.setText("Game:");

        B_OpenVideo.setText("Open Video");
        B_OpenVideo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B_OpenVid_Click(evt);
            }
        });

        TB_BV.setEditable(false);

        L_KeyBV.setText("L_KeyBV");

        B_GoBV.setText("Go");
        B_GoBV.setEnabled(false);
        B_GoBV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dumpBV(evt);
            }
        });

        L_BVTeam.setText("Team:");

        CB_Team.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "My Team" }));
        CB_Team.setEnabled(false);

        B_BKP_BV.setText("Backup BV");
        B_BKP_BV.setEnabled(false);

        RTB_VID.setEditable(false);
        RTB_VID.setColumns(20);
        RTB_VID.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        RTB_VID.setRows(1);
        RTB_VID_Scroll.setViewportView(RTB_VID);

        javax.swing.GroupLayout tab_BVLayout = new javax.swing.GroupLayout(tab_BV);
        tab_BV.setLayout(tab_BVLayout);
        tab_BVLayout.setHorizontalGroup(
            tab_BVLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(RTB_VID_Scroll)
            .addGroup(tab_BVLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tab_BVLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(L_KeyBV, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(tab_BVLayout.createSequentialGroup()
                        .addGroup(tab_BVLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(B_OpenVideo)
                            .addComponent(B_GoBV))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(tab_BVLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(tab_BVLayout.createSequentialGroup()
                                .addComponent(L_BVTeam)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(CB_Team, 0, 131, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(B_BKP_BV))
                            .addComponent(TB_BV))))
                .addContainerGap())
        );
        tab_BVLayout.setVerticalGroup(
            tab_BVLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tab_BVLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tab_BVLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(B_OpenVideo)
                    .addComponent(TB_BV, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(L_KeyBV)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(tab_BVLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(B_GoBV)
                    .addComponent(L_BVTeam)
                    .addComponent(CB_Team, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(B_BKP_BV))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(RTB_VID_Scroll, javax.swing.GroupLayout.DEFAULT_SIZE, 451, Short.MAX_VALUE))
        );

        tab_Main.addTab("Battle Videos", tab_BV);

        B_OpenSAV.setText("Open Save");
        B_OpenSAV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B_OpenSAV_Click(evt);
            }
        });

        TB_SAV.setEditable(false);

        L_KeySAV.setText("L_KeySAV");

        B_GoSAV.setText("Go");
        B_GoSAV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dumpSAV(evt);
            }
        });

        L_BoxSAV.setText("Box:");

        CB_BoxStart.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "All", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31" }));
        CB_BoxStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changeboxsetting(evt);
            }
        });

        B_BKP_SAV.setText("Backup Save");
        B_BKP_SAV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B_BKP_SAV_Click(evt);
            }
        });

        RTB_SAV.setEditable(false);
        RTB_SAV.setColumns(20);
        RTB_SAV.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        RTB_SAV.setRows(1);
        RTB_SAV_Scroll.setViewportView(RTB_SAV);

        CB_BoxEnd.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1" }));

        L_BoxThru.setText("-");

        GB_Filter.setBorder(javax.swing.BorderFactory.createTitledBorder("Filtering"));

        CHK_Egg.setText("Eggs only");

        L_No_IVs.setText("No. of perfect IVs");

        CB_No_IVs.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "0", "1", "2", "3", "4", "5", "6" }));

        CHK_Trickroom.setText("Trickroom");
        CHK_Trickroom.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                toggleTrickroom(evt);
            }
        });

        CHK_Special_Attacker.setText("Special Attacker");
        CHK_Special_Attacker.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                toggleSpecialAttacker(evt);
            }
        });

        L_IVsMiss.setText("These perfect IVs:");

        CHK_IV_HP.setText("HP");
        CHK_IV_HP.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                toggleIVAll(evt);
            }
        });

        CHK_IV_Atk.setText("Attack");
        CHK_IV_Atk.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                toggleIVAll(evt);
            }
        });

        CHK_IV_Def.setText("Defense");
        CHK_IV_Def.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                toggleIVAll(evt);
            }
        });

        CHK_IV_SpAtk.setText("Special Attack");
        CHK_IV_SpAtk.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                toggleIVAll(evt);
            }
        });

        CHK_IV_SpDef.setText("Special Defense");
        CHK_IV_SpDef.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                toggleIVAll(evt);
            }
        });

        CHK_IV_Spe.setText("Speed");
        CHK_IV_Spe.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                toggleIVAll(evt);
            }
        });

        L_HP_Type.setText("HP Type");

        CHK_IVsAny.setText("All");
        CHK_IVsAny.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                toggleIVsAny(evt);
            }
        });

        CHK_Is_Shiny.setText("Shinies only");

        CHK_Hatches_Shiny_For.setText("Has any of these SVs");

        CHK_Hatches_Shiny_For_Me.setText("Has my SV");

        RAD_Gender.add(RAD_Male);
        RAD_Male.setText("♂");

        RAD_Gender.add(RAD_Female);
        RAD_Female.setText("♀");

        RAD_Gender.add(RAD_GenderAny);
        RAD_GenderAny.setSelected(true);
        RAD_GenderAny.setText("Any");

        L_Nature.setText("Nature");

        CHK_Has_HA.setText("Has Hidden Ability");

        L_Ability.setText("Ability");

        javax.swing.GroupLayout GB_FilterLayout = new javax.swing.GroupLayout(GB_Filter);
        GB_Filter.setLayout(GB_FilterLayout);
        GB_FilterLayout.setHorizontalGroup(
            GB_FilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(GB_FilterLayout.createSequentialGroup()
                .addGroup(GB_FilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(GB_FilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(GB_FilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, GB_FilterLayout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addGroup(GB_FilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(CHK_IVsAny, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(L_IVsMiss, javax.swing.GroupLayout.Alignment.TRAILING)))
                            .addGroup(GB_FilterLayout.createSequentialGroup()
                                .addGroup(GB_FilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(CHK_Egg)
                                    .addComponent(CHK_Is_Shiny)
                                    .addComponent(CHK_Hatches_Shiny_For)
                                    .addComponent(CHK_Hatches_Shiny_For_Me)
                                    .addGroup(GB_FilterLayout.createSequentialGroup()
                                        .addContainerGap()
                                        .addComponent(L_No_IVs)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(CB_No_IVs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(0, 19, Short.MAX_VALUE)))
                        .addGroup(GB_FilterLayout.createSequentialGroup()
                            .addComponent(RAD_Male)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(RAD_Female)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(RAD_GenderAny)
                            .addGap(33, 33, 33)))
                    .addGroup(GB_FilterLayout.createSequentialGroup()
                        .addComponent(CHK_Has_HA)
                        .addGap(33, 33, 33)))
                .addGap(14, 14, 14)
                .addGroup(GB_FilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(TB_SVs)
                    .addGroup(GB_FilterLayout.createSequentialGroup()
                        .addComponent(L_Ability)
                        .addGap(8, 8, 8)
                        .addComponent(CB_Abilities, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(GB_FilterLayout.createSequentialGroup()
                        .addComponent(L_HP_Type)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(CCB_HPType, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(GB_FilterLayout.createSequentialGroup()
                        .addGroup(GB_FilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(CHK_Trickroom)
                            .addComponent(CHK_IV_HP)
                            .addComponent(CHK_IV_Atk)
                            .addComponent(CHK_IV_Def))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(GB_FilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(CHK_IV_Spe)
                            .addComponent(CHK_IV_SpDef)
                            .addComponent(CHK_IV_SpAtk)
                            .addComponent(CHK_Special_Attacker))
                        .addGap(0, 5, Short.MAX_VALUE))
                    .addGroup(GB_FilterLayout.createSequentialGroup()
                        .addComponent(L_Nature)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(CCB_Natures, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        GB_FilterLayout.setVerticalGroup(
            GB_FilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(GB_FilterLayout.createSequentialGroup()
                .addGroup(GB_FilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(GB_FilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(CHK_Egg)
                        .addComponent(L_HP_Type))
                    .addComponent(CCB_HPType, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(GB_FilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(L_No_IVs)
                    .addComponent(CB_No_IVs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(CHK_Trickroom)
                    .addComponent(CHK_Special_Attacker))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(GB_FilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(L_IVsMiss)
                    .addComponent(CHK_IV_HP)
                    .addComponent(CHK_IV_SpAtk))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(GB_FilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(CHK_IV_Atk)
                    .addComponent(CHK_IV_SpDef)
                    .addComponent(CHK_IVsAny)
                    .addComponent(CHK_Is_Shiny))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(GB_FilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(CHK_IV_Def)
                    .addComponent(CHK_IV_Spe)
                    .addComponent(CHK_Hatches_Shiny_For_Me))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(GB_FilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(GB_FilterLayout.createSequentialGroup()
                        .addGroup(GB_FilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(CHK_Hatches_Shiny_For)
                            .addComponent(TB_SVs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(GB_FilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(RAD_Male)
                            .addComponent(RAD_Female)
                            .addComponent(RAD_GenderAny)
                            .addComponent(L_Nature)))
                    .addComponent(CCB_Natures, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(GB_FilterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(CHK_Has_HA)
                    .addComponent(L_Ability)
                    .addComponent(CB_Abilities, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        CHK_Enable_Filtering.setText("Enable Filtering");
        CHK_Enable_Filtering.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                toggleFilter(evt);
            }
        });

        L_SAVStats.setOpaque(true);

        javax.swing.GroupLayout tab_SAVLayout = new javax.swing.GroupLayout(tab_SAV);
        tab_SAV.setLayout(tab_SAVLayout);
        tab_SAVLayout.setHorizontalGroup(
            tab_SAVLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(RTB_SAV_Scroll)
            .addGroup(tab_SAVLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tab_SAVLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tab_SAVLayout.createSequentialGroup()
                        .addGroup(tab_SAVLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(B_OpenSAV)
                            .addGroup(tab_SAVLayout.createSequentialGroup()
                                .addComponent(B_GoSAV)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(L_SAVStats)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(tab_SAVLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(tab_SAVLayout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(L_BoxSAV)
                                .addGap(18, 18, 18)
                                .addComponent(CB_BoxStart, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(L_BoxThru)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(CB_BoxEnd, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(B_BKP_SAV))
                            .addComponent(TB_SAV)))
                    .addGroup(tab_SAVLayout.createSequentialGroup()
                        .addComponent(CHK_Enable_Filtering)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(L_KeySAV, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addComponent(GB_Filter, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        tab_SAVLayout.setVerticalGroup(
            tab_SAVLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tab_SAVLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tab_SAVLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(B_OpenSAV)
                    .addComponent(TB_SAV, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(L_KeySAV)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(tab_SAVLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(B_GoSAV)
                    .addComponent(L_BoxSAV)
                    .addComponent(CB_BoxStart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(B_BKP_SAV)
                    .addComponent(CB_BoxEnd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(L_BoxThru)
                    .addComponent(L_SAVStats))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(CHK_Enable_Filtering)
                .addGap(5, 5, 5)
                .addComponent(GB_Filter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(RTB_SAV_Scroll, javax.swing.GroupLayout.DEFAULT_SIZE, 203, Short.MAX_VALUE))
        );

        tab_Main.addTab("Saves", tab_SAV);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Export Options"));

        L_Language.setText("Language:");

        L_ExportStyle.setText("Style:");

        CB_MainLanguage.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "English", "日本語", "Français", "Italiano", "Deutsch", "Español", "한국어" }));
        CB_MainLanguage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changeLanguage(evt);
            }
        });

        CB_ExportStyle.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Default", "Reddit", "TSV", "Custom 1", "Custom 2", "Custom 3", "CSV default", "CSV custom", "To .PK6 File" }));
        CB_ExportStyle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changeExportStyle(evt);
            }
        });

        CHK_MarkFirst.setSelected(true);
        CHK_MarkFirst.setText("Mark Unsure Data With ~");

        CHK_HideFirst.setText("Don't Show Unsure Data");

        CHK_Split.setText("Split Boxes");

        CHK_Header.setSelected(true);
        CHK_Header.setText("Show Header");

        CHK_Unicode.setSelected(true);
        CHK_Unicode.setText("Use Unicode Characters");

        CHK_ShowESV.setText("Show ESV for Hatched Pokemon");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(L_Language)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(CB_MainLanguage, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(L_ExportStyle)
                        .addGap(18, 18, 18)
                        .addComponent(CB_ExportStyle, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(10, 10, 10))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(CHK_MarkFirst)
                    .addComponent(CHK_HideFirst)
                    .addComponent(CHK_Split)
                    .addComponent(CHK_Header)
                    .addComponent(CHK_Unicode)
                    .addComponent(CHK_ShowESV))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(L_Language)
                    .addComponent(CB_MainLanguage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(L_ExportStyle)
                    .addComponent(CB_ExportStyle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(CHK_MarkFirst)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(CHK_HideFirst)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(CHK_Split)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(CHK_Header)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(CHK_Unicode)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(CHK_ShowESV))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Reddit Options"));

        CHK_R_Table.setText("Format as Table");
        CHK_R_Table.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                changeTableStatus(evt);
            }
        });

        CHK_BoldIVs.setSelected(true);
        CHK_BoldIVs.setText("Bold Perfect IVs");

        CHK_ColorBox.setSelected(true);
        CHK_ColorBox.setText("Color Boxes");

        CB_BoxColor.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Cycle", "Default", "Blue", "Green", "Yellow", "Red" }));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(CHK_R_Table)
                    .addComponent(CHK_BoldIVs)
                    .addComponent(CHK_ColorBox))
                .addGap(0, 0, Short.MAX_VALUE))
            .addComponent(CB_BoxColor, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(CHK_R_Table)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(CHK_BoldIVs)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(CHK_ColorBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(CB_BoxColor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("CSV Options"));

        CHK_NameQuotes.setText("<html>Enclose nickname<br>and OT in quotes");

        B_ResetCSV.setText("Reset Custom CSV");
        B_ResetCSV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B_ResetCSV_Click(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(CHK_NameQuotes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addComponent(B_ResetCSV, javax.swing.GroupLayout.DEFAULT_SIZE, 144, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(CHK_NameQuotes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(B_ResetCSV)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Text Format (see \"Help\" for help)"));
        jPanel4.setPreferredSize(new java.awt.Dimension(12, 162));

        RTB_OPTIONS.setEditable(false);
        RTB_OPTIONS.setColumns(20);
        RTB_OPTIONS.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        RTB_OPTIONS.setLineWrap(true);
        RTB_OPTIONS.setRows(1);
        jScrollPane1.setViewportView(RTB_OPTIONS);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 139, Short.MAX_VALUE)
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Text Format Preview"));
        jPanel5.setPreferredSize(new java.awt.Dimension(178, 162));

        RTB_Preview.setEditable(false);
        RTB_Preview.setColumns(20);
        RTB_Preview.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        RTB_Preview.setRows(1);
        RTB_Preview.setMaximumSize(new java.awt.Dimension(164, 18));
        jScrollPane2.setViewportView(RTB_Preview);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 134, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout tab_OptionsLayout = new javax.swing.GroupLayout(tab_Options);
        tab_Options.setLayout(tab_OptionsLayout);
        tab_OptionsLayout.setHorizontalGroup(
            tab_OptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tab_OptionsLayout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(tab_OptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, 365, Short.MAX_VALUE)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, 365, Short.MAX_VALUE)
        );
        tab_OptionsLayout.setVerticalGroup(
            tab_OptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tab_OptionsLayout.createSequentialGroup()
                .addGroup(tab_OptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(tab_OptionsLayout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, 157, Short.MAX_VALUE))
        );

        tab_Main.addTab("Options", tab_Options);

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("Encrypted Saves"));

        L_instrSAV.setText("SAV: File 1 = \"26\", File 2 = \"16\", File 3 = \"165\"");

        B_Break.setText("Break");
        B_Break.setEnabled(false);
        B_Break.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                breakSAV(evt);
            }
        });

        B_File1.setText("File 1");
        B_File1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadBreak1(evt);
            }
        });

        TB_File1.setEditable(false);

        B_File2.setText("File 2");
        B_File2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadBreak2(evt);
            }
        });

        TB_File2.setEditable(false);

        B_File3.setText("File 3");
        B_File3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadBreak3(evt);
            }
        });

        TB_File3.setEditable(false);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(L_instrSAV)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 40, Short.MAX_VALUE)
                .addComponent(B_Break, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(B_File3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)
                    .addComponent(B_File2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(B_File1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(TB_File1)
                    .addComponent(TB_File2)
                    .addComponent(TB_File3)))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(L_instrSAV)
                    .addComponent(B_Break))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(B_File1)
                    .addComponent(TB_File1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(B_File2)
                    .addComponent(TB_File2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(B_File3)
                    .addComponent(TB_File3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder("Improve Decryption for Encrypted Saves"));

        L_instrFolder.setText("Select folder containing encrypted saves:");

        B_BreakFolder.setText("Break");
        B_BreakFolder.setEnabled(false);
        B_BreakFolder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                B_BreakFolder_Click(evt);
            }
        });

        B_Folder.setText("Folder");
        B_Folder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadBreakFolder(evt);
            }
        });

        TB_Folder.setEditable(false);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(B_Folder, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(TB_Folder))
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(L_instrFolder)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 64, Short.MAX_VALUE)
                .addComponent(B_BreakFolder, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(FolderBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(L_instrFolder)
                    .addComponent(B_BreakFolder))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(TB_Folder, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(B_Folder))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(FolderBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder("Battle Videos"));

        L_instrBV.setText("BV: File 1 = \"-1\", File 2 = \"-2\"");

        B_BreakBV.setText("Break");
        B_BreakBV.setEnabled(false);
        B_BreakBV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                breakBV(evt);
            }
        });

        BVFile1.setText("File 1");
        BVFile1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadBreakBV1(evt);
            }
        });

        TB_FileBV1.setEditable(false);

        BVFile2.setText("File 2");
        BVFile2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadBreakBV2(evt);
            }
        });

        TB_FileBV2.setEditable(false);

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(L_instrBV)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(B_BreakBV, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(BVFile2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)
                    .addComponent(BVFile1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(TB_FileBV1)
                    .addComponent(TB_FileBV2)))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(L_instrBV)
                    .addComponent(B_BreakBV))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BVFile1)
                    .addComponent(TB_FileBV1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BVFile2)
                    .addComponent(TB_FileBV2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        javax.swing.GroupLayout tab_BreakerLayout = new javax.swing.GroupLayout(tab_Breaker);
        tab_Breaker.setLayout(tab_BreakerLayout);
        tab_BreakerLayout.setHorizontalGroup(
            tab_BreakerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        tab_BreakerLayout.setVerticalGroup(
            tab_BreakerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tab_BreakerLayout.createSequentialGroup()
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 196, Short.MAX_VALUE))
        );

        tab_Main.addTab("Breaker", tab_Breaker);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(tab_Main)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(label3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(CB_Game, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(B_ShowOptions)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(B_ShowOptions)
                    .addComponent(CB_Game, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tab_Main)
                .addContainerGap())
        );

        tab_Main.getAccessibleContext().setAccessibleName("");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        // Set default system look and feel
        try {
            javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException|InstantiationException|IllegalAccessException|javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Form1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        // Create and display the form
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Form1().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BVFile1;
    private javax.swing.JButton BVFile2;
    private javax.swing.JButton B_BKP_BV;
    private javax.swing.JButton B_BKP_SAV;
    private javax.swing.JButton B_Break;
    private javax.swing.JButton B_BreakBV;
    private javax.swing.JButton B_BreakFolder;
    private javax.swing.JButton B_File1;
    private javax.swing.JButton B_File2;
    private javax.swing.JButton B_File3;
    private javax.swing.JButton B_Folder;
    private javax.swing.JButton B_GoBV;
    private javax.swing.JButton B_GoSAV;
    private javax.swing.JButton B_OpenSAV;
    private javax.swing.JButton B_OpenVideo;
    private javax.swing.JButton B_ResetCSV;
    private javax.swing.JButton B_ShowOptions;
    private javax.swing.JComboBox CB_Abilities;
    private javax.swing.JComboBox CB_BoxColor;
    private javax.swing.JComboBox CB_BoxEnd;
    private javax.swing.JComboBox CB_BoxStart;
    private javax.swing.JComboBox CB_ExportStyle;
    private javax.swing.JComboBox CB_Game;
    private javax.swing.JComboBox CB_MainLanguage;
    private javax.swing.JComboBox CB_No_IVs;
    private javax.swing.JComboBox CB_Team;
    private org.japura.gui.CheckComboBox CCB_HPType;
    private org.japura.gui.CheckComboBox CCB_Natures;
    private javax.swing.JCheckBox CHK_BoldIVs;
    private javax.swing.JCheckBox CHK_ColorBox;
    private javax.swing.JCheckBox CHK_Egg;
    private javax.swing.JCheckBox CHK_Enable_Filtering;
    private javax.swing.JCheckBox CHK_Has_HA;
    private javax.swing.JCheckBox CHK_Hatches_Shiny_For;
    private javax.swing.JCheckBox CHK_Hatches_Shiny_For_Me;
    private javax.swing.JCheckBox CHK_Header;
    private javax.swing.JCheckBox CHK_HideFirst;
    private javax.swing.JCheckBox CHK_IV_Atk;
    private javax.swing.JCheckBox CHK_IV_Def;
    private javax.swing.JCheckBox CHK_IV_HP;
    private javax.swing.JCheckBox CHK_IV_SpAtk;
    private javax.swing.JCheckBox CHK_IV_SpDef;
    private javax.swing.JCheckBox CHK_IV_Spe;
    private javax.swing.JCheckBox CHK_IVsAny;
    private javax.swing.JCheckBox CHK_Is_Shiny;
    private javax.swing.JCheckBox CHK_MarkFirst;
    private javax.swing.JCheckBox CHK_NameQuotes;
    private javax.swing.JCheckBox CHK_R_Table;
    private javax.swing.JCheckBox CHK_ShowESV;
    private javax.swing.JCheckBox CHK_Special_Attacker;
    private javax.swing.JCheckBox CHK_Split;
    private javax.swing.JCheckBox CHK_Trickroom;
    private javax.swing.JCheckBox CHK_Unicode;
    private javax.swing.JProgressBar FolderBar;
    private javax.swing.JPanel GB_Filter;
    private javax.swing.JLabel L_Ability;
    private javax.swing.JLabel L_BVTeam;
    private javax.swing.JLabel L_BoxSAV;
    private javax.swing.JLabel L_BoxThru;
    private javax.swing.JLabel L_ExportStyle;
    private javax.swing.JLabel L_HP_Type;
    private javax.swing.JLabel L_IVsMiss;
    private javax.swing.JLabel L_KeyBV;
    private javax.swing.JLabel L_KeySAV;
    private javax.swing.JLabel L_Language;
    private javax.swing.JLabel L_Nature;
    private javax.swing.JLabel L_No_IVs;
    private javax.swing.JLabel L_SAVStats;
    private javax.swing.JLabel L_instrBV;
    private javax.swing.JLabel L_instrFolder;
    private javax.swing.JLabel L_instrSAV;
    private javax.swing.JRadioButton RAD_Female;
    private javax.swing.ButtonGroup RAD_Gender;
    private javax.swing.JRadioButton RAD_GenderAny;
    private javax.swing.JRadioButton RAD_Male;
    private javax.swing.JTextArea RTB_OPTIONS;
    private javax.swing.JTextArea RTB_Preview;
    private javax.swing.JTextArea RTB_SAV;
    private javax.swing.JScrollPane RTB_SAV_Scroll;
    private javax.swing.JTextArea RTB_VID;
    private javax.swing.JScrollPane RTB_VID_Scroll;
    private javax.swing.JTextField TB_BV;
    private javax.swing.JTextField TB_File1;
    private javax.swing.JTextField TB_File2;
    private javax.swing.JTextField TB_File3;
    private javax.swing.JTextField TB_FileBV1;
    private javax.swing.JTextField TB_FileBV2;
    private javax.swing.JTextField TB_Folder;
    private javax.swing.JTextField TB_SAV;
    private javax.swing.JTextField TB_SVs;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel label3;
    private javax.swing.JPanel tab_BV;
    private javax.swing.JPanel tab_Breaker;
    private javax.swing.JTabbedPane tab_Main;
    private javax.swing.JPanel tab_Options;
    private javax.swing.JPanel tab_SAV;
    // End of variables declaration//GEN-END:variables
}
