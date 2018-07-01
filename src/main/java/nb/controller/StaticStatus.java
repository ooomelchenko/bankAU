package nb.controller;


import java.util.Arrays;
import java.util.List;

final class StaticStatus {
     static final List<String> bidStatusList = Arrays.asList("Перші торги", "Другі торги", "Треті торги", "Четверті торги", "П'яті торги", "Шості торги", "Сьомі торги", "Восьмі торги");
     static final List<String> statusList = Arrays.asList("Новий лот", "Опубліковано", "Оформлення угоди", "Угода укладена");
     static final List<String> bidResultList = Arrays.asList("", "Торги відбулись", "Торги не відбулись", "Торги анульовано");
     static final List<String> fondDecisionsList = Arrays.asList("", "Відправлено до ФГВФО", "Повторно відправлено до ФГВФО", "ВД ФГВФО", "Комітет ФГВФО");
}