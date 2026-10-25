package com.example.smartpantrymanager.model;

public class Recipe {

    private int id;
    private String name;
    private String preparationSteps;
    private String imageName;

    public Recipe(){
    }

    public Recipe(int id, String name, String preparationSteps, String imageName){
        this.id = id;
        this.name = name;
        this.preparationSteps = preparationSteps;
        this.imageName = imageName;
    }

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

    public String getPreparationSteps() {
        return preparationSteps;
    }

    public void setPreparationSteps(String preparationSteps) {
        this.preparationSteps = preparationSteps;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }
}
