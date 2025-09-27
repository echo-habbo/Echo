package net.h4bbo.echo.api.services.navigator;

import net.h4bbo.echo.storage.models.navigator.NavigatorCategoryData;

import java.util.List;

public interface INavigatorService {
    public List<NavigatorCategoryData> getCategories();
}
