package Src.Domain.ServiceOrder;

import java.io.Serializable;
import java.util.Date;

public interface ServiceOrderInterface extends Serializable {
    public void setCode(int code);

    public int getCode();
    
    public void setName(String name);
    
    public String getName();
    
    public void setDescription(String description);

    public String getDescription();

    public void setRequestTime(Date date);

    public Date getRequestTime();

    public String toString();
}
