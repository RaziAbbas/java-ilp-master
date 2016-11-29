package org.interledger.ilp.core.ledger.model;

// TODO: Not yet used
public interface SignedMessage<T> {

  public T getMessage();
  
  public MessageSignature getSignature();
  
}
