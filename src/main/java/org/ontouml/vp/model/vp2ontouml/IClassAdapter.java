package org.ontouml.vp.model.vp2ontouml;

import com.vp.plugin.model.*;
import org.ontouml.vp.model.ontouml.model.Class;
import org.ontouml.vp.utils.StereotypesManager;

public class IClassAdapter implements IAdapter {

  private final IClass clazz;
  private final IDataType datatype;

  public IClassAdapter(IModelElement element) {
    if (element instanceof IClass) {
      clazz = (IClass) element;
      datatype = null;
    } else if (element instanceof IDataType) {
      clazz = null;
      datatype = (IDataType) element;
    } else {
      throw new IllegalArgumentException(
          "Input element must be an instance of IClass or IDataType.");
    }
  }

  public IClassAdapter(IClass clazz) {
    this.clazz = clazz;
    this.datatype = null;
  }

  public IClassAdapter(IDataType datatype) {
    this.clazz = null;
    this.datatype = datatype;
  }

  @Override
  public boolean isEmpty() {
    return clazz == null && datatype == null;
  }

  @Override
  public IModelElement get() {
    if (isClass()) return clazz;
    if (isDatatype()) return datatype;
    return null;
  }

  private boolean isClass() {
    return clazz != null;
  }

  private boolean isDatatype() {
    return datatype != null;
  }

  public boolean isDerived() {
    IModelElement element = get();
    return element.getName() != null && element.getName().trim().startsWith("/");
  }

  public String getName() {
    IModelElement element = get();

    if (isDerived()) {
      return element.getName().trim().substring(1);
    }

    return element.getName();
  }

  public Boolean isExtensional() {
    Object value = getValueOfTaggedValue(StereotypesManager.PROPERTY_IS_EXTENSIONAL);
    return value instanceof String ? Boolean.parseBoolean((String) value) : null;
  }

  public Boolean isPowertype() {
    Object value = getValueOfTaggedValue(StereotypesManager.PROPERTY_IS_POWERTYPE);
    return value instanceof String ? Boolean.parseBoolean((String) value) : null;
  }

  public Integer getOrder() {
    Object value = getValueOfTaggedValue(StereotypesManager.PROPERTY_ORDER);

    Integer order = null;

    try {
      if (value instanceof String) {
        order =
            Class.ORDERLESS_STRING.equals(value)
                ? Class.ORDERLESS
                : Integer.parseInt((String) value);
      }
    } catch (NumberFormatException ignored) {
      System.out.println("Order cannot be converted to an integer or orderless!");
    }

    return order;
  }

  public String[] getRestrictedTo() {
    if (isDatatype()) return new String[] {"abstract"};

    Object value = getValueOfTaggedValue(StereotypesManager.PROPERTY_RESTRICTED_TO);
    return value instanceof String ? ((String) value).split(" ") : null;
  }

  public boolean isAbstract() {
    return isClass() && clazz.isAbstract();
  }

  public IEnumerationLiteral[] toEnumerationLiteralArray() {
    if (isClass()) return clazz.toEnumerationLiteralArray();
    return new IEnumerationLiteral[0];
  }

  public IAttribute[] toAttributeArray() {
    if (isClass()) return clazz.toAttributeArray();
    return new IAttribute[0];
  }
}
