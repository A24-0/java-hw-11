public class ProductType {
    private int id;
    private String name;
    private String bestBefore;

    public ProductType() {
    }

    public ProductType(int id, String name, String bestBefore) {
        this.id = id;
        this.name = name;
        this.bestBefore = bestBefore;
    }
    public ProductType(String name, String bestBefore) {
        this.name = name;
        this.bestBefore = bestBefore;
    }

    // геттеры и сеттеры
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBestBefore() {
        return bestBefore;
    }

    public void setBestBefore(String bestBefore) {
        this.bestBefore = bestBefore;
    }
}
