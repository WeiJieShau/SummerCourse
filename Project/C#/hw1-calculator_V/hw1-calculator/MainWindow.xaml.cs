using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;
using MySql.Data.MySqlClient;

//using TreeTraversalExample;

namespace hw1_calculator
{
    /// <summary>
    /// Interaction logic for MainWindow.xaml
    /// </summary>
    public partial class MainWindow : Window
    {
        private string connectionString = "server=127.0.0.1;port=3306;user id=root;password=1234;database=hw1_calculator;charset=utf8;";
        public MainWindow()
        {
            InitializeComponent();
            this.TextBox1.Text = string.Empty;
        }

        public class TreeNode
        {
            public string Value;
            public TreeNode Left;
            public TreeNode Right;

            public TreeNode(string value)
            {
                Value = value;
                Left = null;
                Right = null;
            }
        }



        private void TextBox_TextChanged(object sender, TextChangedEventArgs e)
        {

        }

        private void input_1_Click(object sender, RoutedEventArgs e)
        {
            this.TextBox1.Text += input_1.Content;
        }

        private void input_2_Click(object sender, RoutedEventArgs e)
        {
            this.TextBox1.Text += input_2.Content;
        }

        private void input_3_Click(object sender, RoutedEventArgs e)
        {
            this.TextBox1.Text += input_3.Content;
        }

        private void input_4_Click(object sender, RoutedEventArgs e)
        {
            this.TextBox1.Text += input_4.Content;
        }

        private void input_5_Click(object sender, RoutedEventArgs e)
        {
            this.TextBox1.Text += input_5.Content;
        }

        private void input_6_Click(object sender, RoutedEventArgs e)
        {
            this.TextBox1.Text += input_6.Content;
        }

        private void input_7_Click(object sender, RoutedEventArgs e)
        {
            this.TextBox1.Text += input_7.Content;
        }

        private void input_8_Click(object sender, RoutedEventArgs e)
        {
            this.TextBox1.Text += input_8.Content;
        }

        private void input_9_Click(object sender, RoutedEventArgs e)
        {
            this.TextBox1.Text += input_9.Content;
        }

        private void input_0_Click(object sender, RoutedEventArgs e)
        {
            this.TextBox1.Text += input_0.Content;
        }

        private void ___btn1_Click(object sender, RoutedEventArgs e)
        {
            this.TextBox1.Text += ___btn1.Content;
        }

        private void ___btn2_Click(object sender, RoutedEventArgs e)
        {
            this.TextBox1.Text += ___btn2.Content;
        }

        private void ___btn3_Click(object sender, RoutedEventArgs e)
        {
            this.TextBox1.Text += ___btn3.Content;
        }

        private void ___btn4_Click(object sender, RoutedEventArgs e)
        {
            this.TextBox1.Text += ___btn4.Content;
        }
        StringBuilder preorderResult = new StringBuilder();
        StringBuilder postorderResult = new StringBuilder();
        double result;
        String Bin_result;
        private void ___btn_Click(object sender, RoutedEventArgs e)
        {
            string input = TextBox1.Text;
            TreeNode root = ParseInputToTree(input);

            if (root != null)
            {
                //Preorder
                //StringBuilder preorderResult = new StringBuilder();
                PreorderTraversal(root, preorderResult);
                Pre_Block.Text = preorderResult.ToString();
                
                //Postorder
                //StringBuilder postorderResult = new StringBuilder();
                PostorderTraversal(root, postorderResult);
                Post_Block.Text = postorderResult.ToString();
                result = EvaluatePostfixExpression(postorderResult.ToString());
                
                Dec_Block.Text = result.ToString();
                Bin_result=ConvertToBinary(result);
                Bin_Block.Text = Bin_result;
                
            }
            else
            {
                MessageBox.Show("Invalid input. Please enter a valid mathematical expression.");
            }
        }

        private void clear_btn_Click(object sender, RoutedEventArgs e)
        {
            this.TextBox1.Text = "";
            this.preorderResult.Clear();
            this.postorderResult.Clear();
            this.Pre_Block.Text = "";
            this.Post_Block.Text = "";
            this.Dec_Block.Text = "";
            this.Bin_Block.Text = "";
            
        }

        private TreeNode ParseInputToTree(string input)
        {
            // 假設 input 是一個合法的中綴表示法的數學表達式
            // 這裡提供一個簡單的示例實現，僅供參考
            char[] operators = { '+', '-', '*', '/' };
            int index = FindLowestPrecedenceOperatorIndex(input, operators);

            if (index != -1)
            {
                TreeNode root = new TreeNode(input[index].ToString());
                root.Left = ParseInputToTree(input.Substring(0, index));
                root.Right = ParseInputToTree(input.Substring(index + 1));
                return root;
            }
            else
            {
                // 如果找不到運算符，表示 input 是一個數字
                return new TreeNode(input);
            }
        }

        private int FindLowestPrecedenceOperatorIndex(string input, char[] operators)
        {
            int lowestPrecedence = int.MaxValue;
            int lowestIndex = -1;
            int currentPrecedence = 0;

            for (int i = input.Length - 1; i >= 0; i--)
            {
                char c = input[i];
                if (c == ')')
                {
                    currentPrecedence++;
                }
                else if (c == '(')
                {
                    currentPrecedence--;
                }
                else if (currentPrecedence == 0 && Array.IndexOf(operators, c) != -1)
                {
                    int precedence = GetPrecedence(c);
                    if (precedence <= lowestPrecedence)
                    {
                        lowestPrecedence = precedence;
                        lowestIndex = i;
                    }
                }
            }

            return lowestIndex;
        }

        private int GetPrecedence(char op)
        {
            switch (op)
            {
                case '+':
                case '-':
                    return 1;
                case '*':
                case '/':
                    return 2;
                default:
                    return 0;
            }
        }

        private void PreorderTraversal(TreeNode node, StringBuilder result)
        {
            if (node == null)
                return;

            result.Append(node.Value + " ");
            PreorderTraversal(node.Left, result);
            PreorderTraversal(node.Right, result);
        }

        private void PostorderTraversal(TreeNode node, StringBuilder result)
        {
            if (node == null)
                return;

            PostorderTraversal(node.Left, result);
            PostorderTraversal(node.Right, result);
            result.Append(node.Value + " ");
        }




        private double EvaluatePostfixExpression(string postfixExpression)
        {
            Stack<double> stack = new Stack<double>();

            foreach (char c in postfixExpression)
            {
                if (char.IsDigit(c))
                {
                    stack.Push(double.Parse(c.ToString()));
                }
                else if (IsOperator(c))
                {
                    double operand2 = stack.Pop();
                    double operand1 = stack.Pop();
                    double result = PerformOperation(c, operand1, operand2);
                    stack.Push(result);
                }
            }

            return stack.Pop();
        }

        private bool IsOperator(char c)
        {
            return c == '+' || c == '-' || c == '*' || c == '/';
        }

        private double PerformOperation(char op, double operand1, double operand2)
        {
            switch (op)
            {
                case '+':
                    return operand1 + operand2;
                case '-':
                    return operand1 - operand2;
                case '*':
                    return operand1 * operand2;
                case '/':
                    return operand1 / operand2;
                default:
                    throw new ArgumentException("Invalid operator: " + op);
            }
        }
        private string ConvertToBinary(double number)
        {
            int intPart = (int)number;
            double fractionalPart = number - intPart;

            string binaryIntPart = Convert.ToString(intPart, 2);

            StringBuilder binaryFractionalPart = new StringBuilder();
            while (fractionalPart > 0)
            {
                if (binaryFractionalPart.Length >= 32) // 處理小數部分的精度，避免無窮循環
                    break;

                fractionalPart *= 2;
                if (fractionalPart >= 1)
                {
                    binaryFractionalPart.Append("1");
                    fractionalPart -= 1;
                }
                else
                {
                    binaryFractionalPart.Append("0");
                }
            }

            return binaryIntPart + (binaryFractionalPart.Length > 0 ? "." + binaryFractionalPart.ToString() : "");
        }
        private void InsertDataIntoDatabase(string input, string inorder, string preorder, string postorder, double decimalResult, string binaryResult)
        {
            try
            {
                using (MySqlConnection connection = new MySqlConnection(connectionString))
                {
                    connection.Open();

                    string insertQuery = "INSERT INTO user_data (Inorder, Preorder, Postorder, Decimal_, Binary_) VALUES (@inorder, @preorder, @postorder, @decimalResult, @binaryResult)";

                    using (MySqlCommand command = new MySqlCommand(insertQuery, connection))
                    {
                        command.Parameters.AddWithValue("@inorder", inorder);
                        command.Parameters.AddWithValue("@preorder", preorder);
                        command.Parameters.AddWithValue("@postorder", postorder);
                        command.Parameters.AddWithValue("@decimalResult", decimalResult);
                        command.Parameters.AddWithValue("@binaryResult", binaryResult);

                        command.ExecuteNonQuery();
                    }

                    MessageBox.Show("Data inserted into the database successfully!");
                    connection.Close();
                }
            }
            catch (Exception ex)
            {
                MessageBox.Show("Error while inserting data into the database: " + ex.Message);
            }
        }

        private void Insert_btn_Click(object sender, RoutedEventArgs e)
        {
            string input = TextBox1.Text;
            TreeNode root = ParseInputToTree(input);

            if (root != null)
            {
                

                // 將計算結果轉換為二進制
                

                // 插入資料到 SQL 資料庫
                InsertDataIntoDatabase(input, postorderResult.ToString(), preorderResult.ToString(), postorderResult.ToString(), result, Bin_result);
            }
            else
            {
                MessageBox.Show("Invalid input. Please enter a valid mathematical expression.");
            }
        }

        private void Button_Click(object sender, RoutedEventArgs e)
        {
            Window1 Q= new Window1();
            

            // 關閉目前的視窗
            this.Close();

            
            Q.Show();
        }
    }

}

