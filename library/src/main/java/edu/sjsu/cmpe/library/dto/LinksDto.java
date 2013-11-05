package edu.sjsu.cmpe.library.dto;

import java.util.ArrayList;
import java.util.List;

public class LinksDto
{
	private List<LinkDto> links=new ArrayList<LinkDto>();
	
	public void addLink(LinkDto link)
	{
		links.add(link);
	}
	
	public List<LinkDto> getLinks()
	{
		return this.links;
	}
	
	public void setLinks(List<LinkDto> links)
	{
		this.links=links;
	}
}