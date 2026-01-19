package dev.miitong.eliminate.client;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;

import java.util.function.Consumer;

public class EliminateDataGenerator implements Consumer<FabricDataGenerator.Pack>
{
    @Override
    public void accept(FabricDataGenerator.Pack pack)
    {
        pack.addProvider(EnglishLanguageProvider::new);
        pack.addProvider(ChineseLanguageProvider::new);
    }

    private static class EnglishLanguageProvider extends FabricLanguageProvider
    {
        public EnglishLanguageProvider(FabricDataGenerator dataGenerator)
        {
            super(dataGenerator, "en_us");
        }

        @Override
        public void generateTranslations(TranslationBuilder translationBuilder)
        {
            translationBuilder.add("eliminate.screen.title", "Eliminate Settings");
            translationBuilder.add("eliminate.option.enabled", "Enabled");
            translationBuilder.add("eliminate.option.verticalCulling", "Vertical Culling");
            translationBuilder.add("eliminate.option.horizontalCulling", "Horizontal Culling");
            translationBuilder.add("eliminate.option.cullingDistance", "Culling Distance");
        }
    }

    private static class ChineseLanguageProvider extends FabricLanguageProvider
    {
        public ChineseLanguageProvider(FabricDataGenerator dataGenerator)
        {
            super(dataGenerator, "zh_cn");
        }

        @Override
        public void generateTranslations(TranslationBuilder translationBuilder)
        {
            translationBuilder.add("eliminate.screen.title", "消除设置");
            translationBuilder.add("eliminate.option.enabled", "启用");
            translationBuilder.add("eliminate.option.verticalCulling", "垂直剔除");
            translationBuilder.add("eliminate.option.horizontalCulling", "水平剔除");
            translationBuilder.add("eliminate.option.cullingDistance", "剔除距离");
        }
    }
}