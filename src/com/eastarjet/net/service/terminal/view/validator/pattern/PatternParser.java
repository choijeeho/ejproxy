package com.eastarjet.net.service.terminal.view.validator.pattern;

import java.util.Stack;

import com.eastarjet.util.StringToolkit;
import com.eastarjet.util.Toolkit;
import com.eastarjet.util.log.Logger;

/**
 * PatternParser
 *  make from pattern like "$0x11;abc%*bcd%c%n %c%c%c%c%c"  to PatternInterpret
 *  
 * @author clouddrd
 *
 */

public class PatternParser 
{
	byte[] buf;
	int position;
	Token current;
	Token root;
	Stack <Token>stack = new Stack<Token>();
	static Logger log = Toolkit.getLogger(PatternParser.class); 
	
	
	public PatternParser (){;}
	
	public   PatternAnalyzer parse(String pattern)
	{
		PatternAnalyzer ret=null;
		buf=convertHex(pattern);
		position=0;
		//Token[] ret=null;
		if(log.isDebugEnabled())log.debug("parse");
		while(position<buf.length)
		{
			Token t;
			int ch = buf[position++];
			if(log.isTraceEnabled())log.trace("ch="+(char)ch+",");//+(char)buf[(position]);
			
			if(ch=='%'){ t=processToken(); }
			else  t =  new CharToken(ch);
			
			if(log.isTraceEnabled())log.trace("token="+t);
			
			if(t instanceof BlockToken) pushToken(t);
			else if(t instanceof BlockEndToken) popToken();
			else addToken(t);
		}
		
		if(log.isTraceEnabled()) traceNode(root);
		
		ret=new PatternAnalyzer();//root.operator);
		ret.setRootOperator(root.operator);
		//configure(ret);
		
		return ret;
	}
	


	
	void addToken(Token token)
	{
		if(root==null)
		{
			root=token;		current=token; 
			return;
		}
		
		Token tcur=current;
		
		if(token instanceof OrToken)
		{
			if(log.isTraceEnabled()) log.trace("or.left add:"+tcur);

			//current node move to or.left
			((OrToken)token).setLeft(tcur);

			//rearrange point of current node
			token.setPrev(tcur.prev); // token.prev=tcur.prev;
			if(tcur.prev!=null)tcur.prev.setNext(token);
			tcur.setNext(null);
			
			//current=OrToken
			tcur=token;
		}
		else if(current instanceof OrToken && ((OrToken)current).right==null)
		{
			if(log.isTraceEnabled()) log.trace("or.right add:"+token);
			((OrToken)current).setRight(token);
			tcur=current;
		}
		else 
		{
			tcur.setNext(token);
			token.setPrev(tcur);
			//token.prev=tcur;
			tcur=token;
		}
		
		current=tcur;
	}
	
	void pushToken(Token t)
	{
		if(log.isTraceEnabled()) log.trace("push:"+t);
		stack.push(current); //current
		
		current=t;
		stack.push(t);  //block Token 
	}
	
	
	void popToken()
	{
		Token t=stack.pop(); //blockToken
		if(log.isTraceEnabled()) log.trace("pop:"+t);
		
		 BlockToken bt=(BlockToken)t;
  		 bt.setChildren(bt.next);
  		 bt.setNext(null);
		 //current = bt;
		 
		 t=stack.pop(); //prev Current Token
		 current=t;
		 
		 addToken(bt);
	}
	
	
	Token processToken()
	{
		Token ret=null;
		int ch=0,dix=0,ndigit=0;
		byte [] digit= new byte[10];
		while(position<buf.length)
		{
			ch=buf[position++];
			if(StringToolkit.isDigit((char)ch))
			 digit[dix++]=(byte)ch;
			else break;
		}
		
		if(dix>0) { try{ ndigit=  Integer.parseInt(new String(digit,0,dix)); }catch (Exception e){}};
		
		if(ch=='(')		 ret = new BlockToken();
		else if(ch==')') ret = new BlockEndToken();
		else if(ch=='n') ret = new NumberToken(ndigit);
		else if(ch=='c') ret = new AlphaToken(ndigit);
		else if(ch=='%') ret = new CharToken('$');
		else if(ch=='*') ret = new ZeroMoreToken();
		else if(ch=='|') ret = new OrToken();
		return ret;
	}
	
	
	void traceNode(Token tk)
	{
		if(log.isTraceEnabled())log.trace("token >>>>>> ");
		Stack<Token> stack=new Stack<Token>();
		while(tk!=null)
		{
			if(log.isTraceEnabled())log.trace("token="+tk);
			if(tk instanceof BlockToken)
			{
				if(tk.next!=null)
				{ 	stack.push(tk.next);
					if(log.isTraceEnabled())log.trace("push: "+tk.next);
				}
				tk=((BlockToken)tk).children;
			}
			else if(tk instanceof OrToken)
			{
				OrToken ort=(OrToken)tk;
				if(ort.next!=null)
				{
					stack.push(ort.next);
					if(log.isTraceEnabled())log.trace("push: "+ort.next);
				}
				stack.push(ort.right);
				if(log.isTraceEnabled())log.trace("push: "+ort.right);
				stack.push(ort.left);
				if(log.isTraceEnabled())log.trace("push: "+ort.left);
				tk=null;
			}
			else 	tk=tk.next;
			
			if(tk==null && !stack.isEmpty())
			{
				tk=stack.pop();
				if(log.isTraceEnabled())log.trace("pop.token="+tk);
			}
		}//while
	}//method
	  byte[] convertHex(String pattern)
	{
		byte[] buf=pattern.getBytes();
		byte[] dbuf=new byte[buf.length];
		int i=0,ti=0,stat=0;
		int h=0,l=0,prev=0,hv=0;
		while(i<buf.length)
		{
			//log.debug("i="+i+",ti="+ti+",prev="+prev+",ch="+(char)buf[i]);
			if(stat==0 && buf[i]!='$')
			{
				dbuf[ti++]=buf[i++]; 
			}
			else if(stat==0 && buf[i]=='$')
			{	stat=1; prev=i; i++ ;  }
			else if(stat==1 )
			{
				if( buf[i]=='0'){stat=2; i++ ;}
				else if( buf[i]=='$')
				{stat=0; dbuf[ti++]='$';i++ ;}
				else stat=-1;
			}
			else if(stat==2 )
			{
				if( buf[i]=='x'){stat=3;i++ ; }
				else stat=-1;
			}
			else if(stat==3 ) 
			{
				if(buf[i]==';'){h=0;i++; stat=4;}
				else { stat=4; h=buf[i++];}
			}
			else if(stat==4 )
			{
				
				l=buf[i++];
				h=(h >= 'a')? 10+h-'a':h-'0';
				l=(l >= 'a')? 10+l-'a':l-'0';
				hv = (byte)(h*16+l);
				stat=5;
			}
			else if(stat==5)
			{
				if(buf[i]==';'){dbuf[ti++]=(byte)hv; i++; stat=0;}
				else { stat=-1;}
			}
			else if(stat==-1)
			{
				for(int ix=prev;ix<=i;ix++)
					dbuf[ti++]=buf[ix];
				i++;
				stat=0;
			}

		}//while
		
		byte []b = new byte[ti];
		System.arraycopy(dbuf,0,b,0,ti);
		return b;
	 
	}
}//class
 

class TokenType
{
	//"$0x01;,  $1n, $$, $c, $b, $* , $( $) $|"
	final int NORMAL=0;
	final int NUMBER=1;
	final int CHAR=2;
	final int SYMBOL=3;
	final int BYTE=4;
	final int ZEROMORE=5; //must have next token
	final int BLOCK_BEGIN=6;
	final int BLOCK_END=7;
	final int OR=9;
}


class Token
{
	Token prev;
	Token next;
	int digit;
	int symbol;
	Operator operator;
	
	void setPrev(Token prev)
	{
		this.prev=prev;
	//	if(operator!=null) operator.setPrevious(prev.operator);}
	}
	Token getPrev(){return prev;}

	void setNext(Token next)
	{
		this.next=next;
		
		if(operator!=null) operator.setNext( (next!=null)?next.operator:null);
	}
	
	Token getNext(){return next;}
	
	public String toString()
	{ 
		
		String name=getClass().getSimpleName();
		//name=name.substring(name.lastIndexOf('.')+1);
		String oname=null;
		if(operator!=null) oname=operator.toTraceString(); 
		String nname=(next!=null)? next.getClass().getSimpleName():null;
		return name+":sym="+(char)symbol +",len="+digit+",next="+nname+",operator={"+oname+"}";
	}
}



class BlockToken extends Token
{
	BlockToken(){operator=new BlockOperator();}
	Token children;
	
	void setChildren(Token children)
	{
		this.children=children;
		if(operator!=null)
			((BlockOperator)operator).setChildren((children!=null)?children.operator:null);
	}
}

class BlockEndToken extends Token
{
	BlockEndToken()
	{
		
	}
}


class AlphaToken extends Token
{
	AlphaToken(int digit) 
	{
		operator=new AlphaOperator(digit);
		this.digit=digit;
	}
}

class NumberToken extends Token
{
	NumberToken(int digit) 
	{
		this.digit=digit;
		operator=new NumberOperator(digit);
	}
}

class CharToken extends Token
{
	CharToken(int symbol)
	{
		this.symbol=symbol;
		operator=new CharOperator(symbol);
	} 
	//public String toString(){ return getClass().getName()+":"+symbol;}
}

class ZeroMoreToken extends Token
{
	ZeroMoreToken()
	{
		operator=new ZeroMoreOperator();
	}
}

class OrToken extends Token
{
	Token left;
	Token right;

	OrToken()
	{
		operator=new OrOperator();
	}
	void setLeft(Token left)
	{
		this.left=left;
		if(operator!=null)
			((OrOperator)operator).setLeft((left!=null)?left.operator:null);
	}
	
	void setRight(Token right)
	{ 
		this.right=right;
		((OrOperator)operator).setRight((right!=null)?right.operator:null);
	}
}
