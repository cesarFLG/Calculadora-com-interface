package com.example.calcoringinterface;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.collections.FXCollections;

public class CalculadoraController {
    @FXML
    private Label welcomeText;

    @FXML
    private ChoiceBox<String> Diametro;

    @FXML
    private Button calcularButton;

    @FXML
    private Button botaoTabela;

    @FXML
    private TextField campoMaior;

    @FXML
    private TextField campoMenor;

    @FXML
    private TextArea areaResultado;

    private TabelaOring tabelaOring;

    @FXML
    private void initialize() {

        tabelaOring = new TabelaOring();


        Diametro.setItems(FXCollections.observableArrayList(
                "1.78 mm",
                "2.62 mm",
                "3.53 mm"
        ));



        calcularButton.setOnAction(event -> calcularORing());
        botaoTabela.setOnAction(event -> mostrarTabelaOrings());

        areaResultado.setEditable(false);
        areaResultado.setWrapText(true);
    }

    private void mostrarTabelaOrings() {
        try {
            // Verificar se uma espessura foi selecionada
            if (Diametro.getValue() == null || Diametro.getValue().isEmpty()) {
                areaResultado.setText("❌ Selecione uma espessura para ver a tabela!");
                return;
            }

            double espessura = getEspessuraSelecionada();
            StringBuilder tabela = new StringBuilder();

            tabela.append("=== TABELA O-RINGS - ESPESSURA ").append(espessura).append(" mm ===\n\n");

            // Buscar todos os O-rings da espessura selecionada
            java.util.List<Oring> orings = getOringsPorEspessura(espessura);

            if (orings != null && !orings.isEmpty()) {
                // Cabeçalho da tabela
                tabela.append(String.format("%-10s %-12s %-12s\n", "CÓDIGO", "DIÂMETRO", "ESPESSURA"));
                tabela.append("------------------------------------\n");

                // Lista todos os O-rings
                for (Oring oring : orings) {
                    tabela.append(String.format("%-10s %-12.2f %-12.2f\n",
                            oring.codigo,
                            oring.diametro,
                            oring.espessura));
                }

                tabela.append("\n📊 Total: ").append(orings.size()).append(" O-rings disponíveis");
            } else {
                tabela.append("❌ Nenhum O-ring encontrado para esta espessura.");
            }

            areaResultado.setText(tabela.toString());

        } catch (Exception e) {
            areaResultado.setText("❌ ERRO ao carregar tabela: " + e.getMessage());
        }
    }

    // MÉTODO NOVO: Buscar O-rings por espessura
    private java.util.List<Oring> getOringsPorEspessura(double espessura) {
        switch ((int)(espessura * 100)) {
            case 178:
                return tabelaOring.getTabela178();
            case 262:
                return tabelaOring.getTabela262();
            case 353:
                return tabelaOring.getTabela353();
            default:
                return null;
        }
    }

    private double getEspessuraSelecionada() {
        String valor = Diametro.getValue();
        return Double.parseDouble(valor.replace(" mm", ""));
    }

    private void calcularORing() {
        try {

            if (Diametro.getValue() == null || Diametro.getValue().isEmpty()) {
                areaResultado.setText("❌ ERRO: Selecione uma espessura de O-ring!");
                return;
            }

            // 2. Validar se as medidas foram digitadas
            if (campoMaior.getText().isEmpty() || campoMenor.getText().isEmpty()) {
                areaResultado.setText("❌ ERRO: Digite as medidas maior e menor!");
                return;
            }

            double medidaMaior = Double.parseDouble(campoMaior.getText());
            double medidaMenor = Double.parseDouble(campoMenor.getText());
            double espessura = getEspessuraSelecionada();


            if (medidaMaior <= 0 || medidaMenor <= 0) {
                areaResultado.setText("❌ ERRO: Medidas devem ser positivas!");
                return;
            }

            if (medidaMenor >= medidaMaior) {
                areaResultado.setText("❌ ERRO: Medida menor deve ser MENOR que a maior!");
                return;
            }


            double perimetroTotal = 2 * (medidaMaior - medidaMenor) + Math.PI * medidaMenor;
            double diametro = perimetroTotal / Math.PI;


            Oring oringRecomendado = tabelaOring.buscarOringMaisProximo(diametro, espessura);


            StringBuilder resultado = new StringBuilder();
            resultado.append("=== RESULTADOS ===\n\n");
            resultado.append(String.format("Medida Maior: %.2f mm\n", medidaMaior));
            resultado.append(String.format("Medida Menor: %.2f mm\n", medidaMenor));
            resultado.append(String.format("Espessura: %.2f mm\n\n", espessura));

            resultado.append(String.format("📐 Perímetro Total: %.2f mm\n", perimetroTotal));
            resultado.append(String.format("⚙️ Diâmetro Equivalente: %.2f mm\n\n", diametro));

            if (oringRecomendado != null) {
                resultado.append("=== O-RING RECOMENDADO ===\n\n");
                resultado.append(String.format("Código: %s\n", oringRecomendado.codigo));
                resultado.append(String.format("Diâmetro: %.2f mm\n", oringRecomendado.diametro));
                resultado.append(String.format("Espessura: %.2f mm\n", oringRecomendado.espessura));

                double diferenca = Math.abs(oringRecomendado.diametro - diametro);
                resultado.append(String.format("\nDiferença: ±%.2f mm\n", diferenca));

                if (diferenca <= 1.0) {
                    resultado.append("\n✅ AJUSTE PERFEITO!");
                } else if (diferenca <= 3.0) {
                    resultado.append("\n⚠️  AJUSTE ACEITÁVEL");
                } else {
                    resultado.append("\n🔍 CONSIDERE OUTRAS OPÇÕES");
                }
            } else {
                resultado.append("❌ Nenhum O-ring encontrado para esta espessura.");
            }

            areaResultado.setText(resultado.toString());

        } catch (NumberFormatException e) {
            areaResultado.setText("❌ ERRO: Digite apenas números válidos nos campos!");
        } catch (Exception e) {
            areaResultado.setText("❌ ERRO: " + e.getMessage());
        }
    }
}