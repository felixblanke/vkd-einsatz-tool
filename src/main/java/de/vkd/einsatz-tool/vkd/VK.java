package de.vkd.einsatz_tool.vkd;

import java.util.ArrayList;
import java.util.List;

public class VK{
	private static int currID = 0;
	public static int getCurrID() {
		return currID;
	}
	
	private String name, surname;
	private Rank rank;
	private int group;
	private int position;
	private int ID;
	private boolean driver = false;
	private boolean selected = false;
	private Status status = Status.NONE;
	private List<Kuerzung> kuerzungsListe = new ArrayList<Kuerzung>();
	private String remark = "";
	private VK ersatz;
	
	//constructors
	public VK(int group, Rank rank, String name, String surname, int position, boolean driver) {
		currID++;
		this.name = name;
		this.surname = surname;
		this.group = group;
		this.rank = rank;
		this.position = position;
		this.driver = driver;
		this.ID = currID;
	}
	
	//copies the parameter, but creates a 'clean' copy
	public VK(VK vk) {
		this.name = vk.getName();
		this.surname = vk.getSurname();
		this.group = vk.getGroup();
		this.rank = vk.getRank();
		this.position = vk.getPosition();
		this.driver = vk.isDriver();
		this.ID = vk.getID();
	}
	
	
	//methods
	@Override
	public String toString() {
		String s = " || ";
		String kuerzung = "";
		for(Kuerzung k: getKuerzungsListe()) {
			kuerzung += "(" + k.getID() + ", " + k.getPercentage() + "%, " + k.getReason() + "), ";
		}
		if(kuerzung.length() > 2)kuerzung = kuerzung.substring(0, kuerzung.length() -2);
		
		if(status.equals(Status.ERSATZ) && ersatz!=null) 
				return (Main.outputGroup(group) +s+ Main.getRankString(rank) +s+ Main.outputPosition(position) +s+ this.name +s+ this.surname +s+ status.getListName() +s+ ersatz.getStringRepresentation() +s+ kuerzung +s+ getRemark());
		return (Main.outputGroup(group) +s+ Main.getRankString(rank) +s+ Main.outputPosition(position) +s+ this.name +s+ this.surname +s+ status.getListName() +s+ kuerzung +s+ getRemark());
	}
	//getter and setter
	public int getGroup() {
		return group;
	}
	public Rank getRank() {
		return rank;
	}
	public int getPosition() {
		return position;
	}
	public String getName() {
		return name;
	}
	public String getSurname() {
		return surname;
	}
	public int getID() {
		return ID;
	}
	public Status getStatus() {
		return status;
	}
	public String getRemark() {
		return remark;
	}
	public List<Kuerzung> getKuerzungsListe() {
		return kuerzungsListe;
	}
	public VK getErsatz() {
		return ersatz;
	}
	public boolean isDriver(){
		return driver;
	}
	public boolean hasAttendedEinsatz() {
		return getStatus().equals(Status.EINGETEILT) || getStatus().equals(Status.ERSATZ) || getStatus().equals(Status.ZUSAETZLICH);
	}
	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean selected){
		this.selected = selected;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	public void setErsatz(VK ersatz) {
		this.ersatz = ersatz;
	}
	public String getStringRepresentation(){
		return Main.getRankString(getRank()).concat(" ").concat(getName()).concat(" ").concat(getSurname());
	}
}
