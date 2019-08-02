package it.bz.noi.tourism.ninja.utils.miniparser;

public interface ConsumerExtended extends Consumer {
	boolean before(Token t);
	boolean after(Token t);
}
