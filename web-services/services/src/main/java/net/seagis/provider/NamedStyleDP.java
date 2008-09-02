
package net.seagis.provider;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.NamingException;
import net.seagis.provider.sld.SLDFileNamedLayerDP;

import net.seagis.ws.rs.WebService;
import org.geotools.style.MutableStyle;

/**
 * Main Data provider for styles objects. This class act as a proxy for
 * several SLD folder providers.
 * 
 * @author Johann Sorel (Geomatys)
 */
public class NamedStyleDP implements DataProvider<String,MutableStyle>{

    private static String KEY_SLD_DP = "sld_folder";
    
    private static NamedStyleDP instance = null;
    
    private final Collection<DataProvider<String,MutableStyle>> dps = new ArrayList<DataProvider<String,MutableStyle>>();
    
    private NamedStyleDP(){
        
        List<File> folders = getSLDFolders();
        for(File folder : folders){
            SLDFileNamedLayerDP sldDP = new SLDFileNamedLayerDP(folder);
            dps.add(sldDP);
        }
        
    }
    
    /**
     * {@inheritDoc }
     */
    public Class<String> getKeyClass() {
        return String.class;
    }

    /**
     * {@inheritDoc }
     */
    public Class<MutableStyle> getValueClass() {
        return MutableStyle.class;
    }

    /**
     * {@inheritDoc }
     */
    public Set<String> getKeys() {
        Set<String> keys = new HashSet<String>();
        for(DataProvider<String,MutableStyle> dp : dps){
            keys.addAll( dp.getKeys() );
        }
        return keys;
    }

    /**
     * {@inheritDoc }
     */
    public boolean contains(String key) {
        for(DataProvider<String,MutableStyle> dp : dps){
            if(dp.contains(key)) return true;
        }
        return false;
    }

    /**
     * {@inheritDoc }
     */
    public MutableStyle get(String key) {
        MutableStyle style = null;
        for(DataProvider<String,MutableStyle> dp : dps){
            style = dp.get(key);
            if(style != null) return style;
        }
        return null;
    }

    /**
     * {@inheritDoc }
     */
    public void reload() {
        for(DataProvider<String,MutableStyle> dp : dps){
            dp.reload();
        }
    }

    /**
     * {@inheritDoc }
     */
    public void dispose() {
        for(DataProvider<String,MutableStyle> dp : dps){
            dp.dispose();
        }
        dps.clear();
    }

    /**
     * 
     * @return List of folders holding sld files
     */
    private static List<File> getSLDFolders(){
        List<File> folders = new ArrayList<File>();
        
        String strFolders = "";
        try{
            strFolders = WebService.getPropertyValue(JNDI_GROUP,KEY_SLD_DP);
        }catch(NamingException ex){
            Logger.getLogger(NamedStyleDP.class.toString()).log(Level.WARNING, "Serveur property has not be set : "+JNDI_GROUP +" - "+ KEY_SLD_DP);
        }
        
//        strFolders = "/home/sorel/GIS_DATA/Styles";
        
        StringTokenizer token = new StringTokenizer(strFolders,";");
        while(token.hasMoreElements()){
            String path = token.nextToken();
            File f = new File(path);
            if(f.exists() && f.isDirectory()){
                folders.add(f);
            }else{
                Logger.getLogger(NamedStyleDP.class.toString()).log(Level.WARNING, "SLD folder provided is unvalid : "+ path);
            }
        }
        
        return folders;
    }
    
    public static NamedStyleDP getInstance(){
        
        if(instance == null){
            instance = new NamedStyleDP();
        }
        
        return instance;
    }
    
}
