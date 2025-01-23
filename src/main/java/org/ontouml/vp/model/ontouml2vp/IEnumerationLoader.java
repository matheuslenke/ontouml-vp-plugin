package org.ontouml.vp.model.ontouml2vp;

import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IEnumerationLiteral;
import org.ontouml.vp.model.ontouml.model.Class;
import org.ontouml.vp.model.ontouml.model.Literal;

public class IEnumerationLoader {

  public static void importLiterals(Class fromClass) {
    if (!fromClass.isEnumeration()) return;

    IClass toClass = LoaderUtils.getToClass(fromClass);

    if (toClass == null) return;

    fromClass.getLiterals().forEach(l -> importLiteral(toClass, l));
  }

  public static void importLiteral(IClass toClass, Literal fromLiteral) {
    IEnumerationLiteral toLiteral = toClass.createEnumerationLiteral();
    LoaderUtils.loadName(fromLiteral, toLiteral);
    ITaggedValueLoader.loadTaggedValues(fromLiteral, toLiteral);
  }
}
