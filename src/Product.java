import java.math.BigDecimal;
public class Product {
    private int Id;
    private String name;
    private int typeId;
    private String produce;
    private BigDecimal price;

    public Product() {
    }

    public Product(int Id, String name, int typeId, String produce, BigDecimal price) {
        this.Id = Id;
        this.name = name;
        this.typeId = typeId;
        this.produce = produce;
        this.price = price;
    }
    public Product(String name, int typeId, String produce, BigDecimal price) {
        this.name = name;
        this.typeId = typeId;
        this.produce = produce;
        this.price = price;
    }

    // геттеры и сеттеры
    public int getId() {
        return Id;
    }
    public void setId(int id) {
        this.Id = Id;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public String getProduce() {
        return produce;
    }

    public void setProduce(String produce) {
        this.produce = produce;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
