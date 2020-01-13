package com.hxd.utils;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ReadXML {

    public static List<Area> readXML(String filePath) {
        List<Area> list = new ArrayList<>();
        SAXReader reader = new SAXReader();
        try {
            Document doc = reader.read(new File(filePath));
            List<Node> nodeList1 = doc.selectNodes("/OPENAIP/AIRSPACES/ASP/ID");
            List<Integer> ids=new ArrayList<>();
            for (Node node : nodeList1) {
                ids.add(Integer.valueOf(node.getText()));
                //System.out.println(node.getName()+" "+node.getText());
                //System.out.println(node.getName() + ":" + node.valueOf("@ID") );
            }

            List<Node> nodeList2 = doc.selectNodes("/OPENAIP/AIRSPACES/ASP/COUNTRY");
            List<String> country=new ArrayList<>();
            for(Node node :nodeList2){
                country.add(node.getText());
            }

            List<Node> nodeList3 = doc.selectNodes("/OPENAIP/AIRSPACES/ASP/NAME");
            List<String> name=new ArrayList<>();
            for(Node node :nodeList3){
                name.add(node.getText());
            }

            List<Node> nodeList4 = doc.selectNodes("/OPENAIP/AIRSPACES/ASP/GEOMETRY/POLYGON");
            List<String> geometry=new ArrayList<>();
            for(Node node :nodeList4){
                geometry.add(node.getText());
            }


            for(int i=0;i<ids.size();i++){
                Area area = new Area();
                area.setId(ids.get(i));
                area.setCountry(country.get(i));
                area.setGeometry(geometry.get(i));
                area.setName(name.get(i));
                list.add(area);
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return list;
    }
}

class Area{
    private int id;
    private String country;
    private String name;
    private String geometry;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGeometry() {
        return geometry;
    }

    public void setGeometry(String geometry) {
        this.geometry = geometry;
    }

    @Override
    public String toString() {
        return "Area{" +
                "id=" + id +
                ", country='" + country + '\'' +
                ", name='" + name + '\'' +
                ", geometry='" + geometry + '\'' +
                '}';
    }
}
