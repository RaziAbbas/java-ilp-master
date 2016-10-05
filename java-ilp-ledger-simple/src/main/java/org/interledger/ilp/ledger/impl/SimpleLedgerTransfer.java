package org.interledger.ilp.ledger.impl;

import org.interledger.ilp.core.InterledgerPacketHeader;
import org.interledger.ilp.core.LedgerTransfer;

public class SimpleLedgerTransfer implements LedgerTransfer{

	String fromAccount;
	String toAccount;
	String amount;
	String noteToSelf;
	String expirationDate;
	String data;

	@Override
	public String getAmount() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getFromAccount() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InterledgerPacketHeader getHeader() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getNoteToSelf() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getToAccount() {
		// TODO Auto-generated method stub
		return null;
	}

}
