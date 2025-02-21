package org.ontouml.vp.model.vp2ontouml;

import com.vp.plugin.model.IClass;
import com.vp.plugin.model.IDataType;
import com.vp.plugin.model.IModelElement;
import org.ontouml.ontouml4j.model.*;
import org.ontouml.ontouml4j.model.Class;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class IClassTransformer {
  private static final List<String> VP_PRIMITIVE_DATATYPES =
      List.of(
          "boolean", "byte", "char", "double", "float", "int", "long", "short", "string", "void");

  public static Class transform(IModelElement sourceElement, Project project) {
    if (!(sourceElement instanceof IClass) && !(sourceElement instanceof IDataType)) {
      return null;
    }

    if (sourceElement instanceof IDataType) {
      IDataType sourceDatatype = (IDataType) sourceElement;
      //      sourceDatatype.
    }

    IClassAdapter source = new IClassAdapter(sourceElement);
    Class target = new Class();

    IModelElementTransformer.transform(source.get(), target);
    ITaggedValueTransformer.transform(source.get(), target);
    IStereotypeTransformer.transform(source.get(), target);

    String name = source.getName();
    target.setName(new MultilingualText(name));

    boolean isAbstract = source.isAbstract();
    target.setAbstract(isAbstract);

    boolean isDerived = source.isDerived();
    target.setDerived(isDerived);

    Boolean isExtensional = source.isExtensional();
    // target.setIsExtensional(isExtensional);

    Boolean isPowertype = source.isPowertype();
    target.setPowertype(isPowertype != null ? isPowertype : false);

    Integer order = source.getOrder();
    target.setOrder(order == null ? null : String.valueOf(order));
    project.addClass(target);

    String[] restrictedTo = source.getRestrictedTo();
    if (restrictedTo != null) {
      List<Optional<Nature>> natures = Stream.of(restrictedTo).map(Nature::findByName).collect(Collectors.toList());
      target.setRestrictedToNatures(natures.stream().filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList()));
    }

    List<Property> attributes = transformAttributes(source, project);
    attributes.forEach(target::addAttribute);

    List<Literal> literals = transformLiterals(source);
    literals.forEach(project::addLiteral);
    literals.forEach(item -> item.setProjectContainer(project));
    List<String> literalIds = literals.stream().map(Literal::getId).collect(Collectors.toList());
    target.setLiterals(literalIds);

    return target;
  }

  public static List<Literal> transformLiterals(IClassAdapter clazz) {
    return Stream.of(clazz.toEnumerationLiteralArray())
        .map(IEnumerationLiteralTransformer::transform)
        .collect(Collectors.toList());
  }

  public static List<Property> transformAttributes(IClassAdapter clazz, Project project) {
    return Stream.of(clazz.toAttributeArray())
        .map(item -> IPropertyTransformer.transform(item, project))
        .collect(Collectors.toList());
  }
}
