/**   
 * <p><h1>Copyright:</h1><strong><a href="http://www.smart-f.cn">
 * BeiJing Smart Future Technology Co.Ltd. 2015 (c)</a></strong></p>
 */
package smart.photoutil.media;

import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

/**  
 * <p><h1>Copyright:</h1><strong><a href="http://www.smart-f.cn">
 * BeiJing Smart Future Technology Co.Ltd. 2015 (c)</a></strong></p> 
 *
 * <p>
 * <h1>Reviewer:</h1> 
 * <a href="mailto:jiangjunjie@smart-f.cn">jjj</a>
 * </p>
 * 
 * <p>
 * <h1>History Trace:</h1>
 * <li> 2017年10月6日 下午4:15:40    V1.0.0          jjj         first release</li>
 * </p> 
 * @Title VideoProduceConfig.java 
 * @Description please add description for the class 
 * @author jjj
 * @email <a href="jiangjunjie@smart-f.cn">jiangjunjie@smart-f.cn</a>
 * @date 2017年10月6日 下午4:15:40 
 * @version V1.0   
 */
public class VideoProduceConfig {

    private String srcURL                           = "";
    private List<Pair<Float, Float>> partList       = new ArrayList<Pair<Float,Float>>();
    private List<Integer> transVideoIndexList       = new ArrayList<Integer>();
    private String bgmName                          = "";
    private List<Integer> picIndexList              = new ArrayList<Integer>();

    public String getSrcURL() {
        return srcURL;
    }

    public void setSrcURL(String srcURL) {
        this.srcURL = srcURL;
    }

    public List<Pair<Float, Float>> getPartList() {
        return partList;
    }

    public void setPartList(List<Pair<Float, Float>> partList) {
        this.partList = partList;
    }

    public List<Integer> getTransVideoIndexList() {
        return transVideoIndexList;
    }

    public void setTransVideoIndexList(List<Integer> transVideoIndexList) {
        this.transVideoIndexList = transVideoIndexList;
    }

    public String getBgmName() {
        return bgmName;
    }

    public void setBgmName(String bgmName) {
        this.bgmName = bgmName;
    }

    public List<Integer> getPicIndexList() {
        return picIndexList;
    }

    public void setPicIndexList(List<Integer> picIndexList) {
        this.picIndexList = picIndexList;
    }

//    @Override
//    public String toString() {
//        return ToStringBuilder.reflectionToString(this);
//    }
}
