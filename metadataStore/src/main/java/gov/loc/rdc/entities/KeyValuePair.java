package gov.loc.rdc.entities;

import java.util.Objects;

public class KeyValuePair<K, V> {
  private K key;
  private V value;

  public KeyValuePair() {
  }

  public KeyValuePair(K key, V value) {
    this.key = key;
    this.value = value;
  }

  @Override
  public int hashCode() {
    return Objects.hash(key, value);
  }

  @SuppressWarnings("rawtypes")
  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    KeyValuePair other = (KeyValuePair) obj;
    return Objects.equals(this.key, other.key) && Objects.equals(this.value, other.value);
  }

  public K getKey() {
    return key;
  }

  public void setKey(K key) {
    this.key = key;
  }

  public V getValue() {
    return value;
  }

  public void setValue(V value) {
    this.value = value;
  }
}
