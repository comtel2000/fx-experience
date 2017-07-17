package org.comtel2000.keyboard.control.table;

import java.util.Map;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.util.converter.DefaultStringConverter;

public class KeyboardTableCell<S, T> extends TextFieldTableCell<S, T> {

  public static <S, T> Callback<TableColumn<S, T>, TableCell<S, T>> forTableColumn(
      final StringConverter<T> converter, final Map<Object, Object> properties) {
    return list -> {
      TextFieldTableCell<S, T> cell = new TextFieldTableCell<>(converter);
      cell.getProperties().putAll(properties);
      return cell;
    };
  }

  public static <S> Callback<TableColumn<S, String>, TableCell<S, String>> forTableColumn(
      final Map<Object, Object> properties) {
    return forTableColumn(new DefaultStringConverter(), properties);
  }

  public KeyboardTableCell() {
    super();
  }

  public KeyboardTableCell(StringConverter<T> converter) {
    super(converter);
  }
}
