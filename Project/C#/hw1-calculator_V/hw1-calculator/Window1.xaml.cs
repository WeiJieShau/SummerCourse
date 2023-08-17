using MySql.Data.MySqlClient;
using System;
using System.Collections.Generic;
using System.Windows;
using System.Windows.Controls;

namespace hw1_calculator
{
    /// <summary>
    /// Window1.xaml 的互動邏輯
    /// </summary>
    public partial class Window1 : Window
    {
        private string connectionString = "server=127.0.0.1;port=3306;user id=root;password=;database=hw1_calculator;charset=utf8;";
        private List<TreeNodeData> treeNodes = new List<TreeNodeData>();
        public Window1()
        {
            InitializeComponent();
            LoadDataFromDatabase();
        }
        public class User
        {
            public string Name { get; set; }

            public int Age { get; set; }
            public override string ToString()
            {
                return this.Name + ", " + this.Age + " years old";
            }
        }
        private void LoadDataFromDatabase()
        {
            try
            {
                using (MySqlConnection connection = new MySqlConnection(connectionString))
                {
                    connection.Open();

                    string query = "SELECT Inorder, Preorder, Postorder, Decimal_, Binary_ FROM user_data";

                    using (MySqlCommand command = new MySqlCommand(query, connection))
                    {
                        using (MySqlDataReader reader = command.ExecuteReader())
                        {
                            while (reader.Read())
                            {
                                string inorder = reader.GetString("Inorder");
                                string preorder = reader.GetString("Preorder");
                                string postorder = reader.GetString("Postorder");
                                int decimalValue = reader.GetInt32("Decimal_");
                                string binaryValue = reader.GetString("Binary_");

                                treeNodes.Add(new TreeNodeData()
                                {
                                    Inorder = inorder,
                                    Preorder = preorder,
                                    Postorder = postorder,
                                    DecimalValue = decimalValue,
                                    BinaryValue = binaryValue
                                });
                            }
                        }
                    }

                    List1.ItemsSource = treeNodes;
                }
            }
            catch (Exception ex)
            {
                MessageBox.Show("Error while loading data from the database: " + ex.Message);
            }
        }
        public class TreeNodeData
        {
            public string Inorder { get; set; }
            public string Preorder { get; set; }
            public string Postorder { get; set; }
            public int DecimalValue { get; set; }
            public string BinaryValue { get; set; }

            public override string ToString()
            {
                return $"Inorder: {Inorder}, Preorder: {Preorder}, Postorder: {Postorder}, Decimal: {DecimalValue}, Binary: {BinaryValue}";
            }
        }
        private void DeleteButton_Click(object sender, RoutedEventArgs e)
        {
            
        }

        private void ListView_SelectionChanged(object sender, SelectionChangedEventArgs e)
        {
            
        }

        private void DeleteButton_Click_1(object sender, RoutedEventArgs e)
        {
            if (List1.SelectedIndex >= 0)
            {
                TreeNodeData selectedNode = List1.SelectedItem as TreeNodeData;

                try
                {
                    using (MySqlConnection connection = new MySqlConnection(connectionString))
                    {
                        connection.Open();

                        string query = "DELETE FROM user_data WHERE Inorder = @Inorder"; // 請替換成你的資料庫表名稱

                        using (MySqlCommand command = new MySqlCommand(query, connection))
                        {
                            command.Parameters.AddWithValue("@Inorder", selectedNode.Inorder);
                            command.ExecuteNonQuery();
                        }
                    }

                    treeNodes.Remove(selectedNode);
                    List1.Items.Refresh();
                }
                catch (Exception ex)
                {
                    MessageBox.Show("Error while deleting data from the database: " + ex.Message);
                }
            }
        }

        private void Back_btn_Click(object sender, RoutedEventArgs e)
        {
            MainWindow mainWindow = new MainWindow();

            // 關閉目前的視窗
            this.Close();

            // 顯示 MainWindow
            mainWindow.Show();
        }
    }
}
