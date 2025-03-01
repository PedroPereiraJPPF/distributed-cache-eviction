package Src.Domain.ServiceOrder;

import java.util.Date;

public class ServiceOrder implements ServiceOrderInterface {
    private static int codeCount = 0;
    private int code;
    private String name;
    private String description;
    private Date requestTime;

    public ServiceOrder() {
        codeCount++;
        this.code = codeCount;
        this.requestTime = new Date();
    }

    public ServiceOrder(int code) {
        this.code = code;
        this.requestTime = new Date();
    }

    public ServiceOrder(int code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.requestTime = new Date();
    }

    @Override
    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public int getCode() {
        return this.code;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public void setRequestTime(Date date) {
        this.requestTime = date;
    }

    @Override
    public Date getRequestTime() {
        return this.requestTime;
    }
 
    public String toString() {
        return "{ codigo: " + this.code + ", nome: " + this.name + ", descricao: " + this.description + ", hora da requisição: " + this.requestTime + " }";
    }
}
