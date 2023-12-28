#include <iostream>

void test();
int quickMaths(int input);

int main() {
    std::cout << "Hello, Client!" << std::endl;
    test();
    return 0;
}

void test() {
    std::cout << "Test method called" << std::endl;
    std::cout << quickMaths(std::cin.get());
}

int quickMaths(int input) {
    return input + std::cin.get();
}